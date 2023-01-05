package hana04.yuri.trial.t00;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import hana04.distrib.app.App;
import hana04.distrib.app.Main;
import hana04.distrib.app.Server;
import hana04.distrib.request.Request;
import hana04.distrib.request.state.ConcreteRunnerContext;
import hana04.distrib.request.state.RequestRunnerContext;
import hana04.distrib.request.state.RequestRunnerState;
import hana04.distrib.request.state.TerminatingState;
import hana04.distrib.request.workblock.SimpleBlock2DGenerator;
import hana04.distrib.request.workblock.WorkBlockRunnerState2D;
import hana04.distrib.util.TimingInfoWriter;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.FilmStorage;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.film.simplergb.SimpleRgbFilm;
import hana04.yuri.film.simplergb.SimpleRgbFilmBuilder;
import hana04.yuri.request.params.BlockRendererParameters;
import hana04.yuri.sampler.IndependentSamplerBuilder;
import hana04.yuri.sampler.PerPixelSampler;
import hana04.yuri.sampler.Sampler;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.vecmath.Vector2d;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FractalImageRequestRunner implements Request.Runner {
  private static Logger logger = LoggerFactory.getLogger(FractalImageRequestRunner.class);
  /**
   * Computed fields.
   */
  private SimpleRgbFilm film;
  private FilmStorage filmStorage;
  private Sampler sampler;
  private PerPixelSampler perPixelSampler;
  private long startMillis;
  private Timer saveTimer;
  private BlockRendererParameters requestParameters;
  /**
   * Assigned fields.
   */
  private final FractalImageRequest request;
  private final FileSystem fileSystem;
  private Provider<SimpleRgbFilmBuilder> simpleRgbFilmBuilderProvider;
  private Provider<IndependentSamplerBuilder> independentSamplerBuilderProvider;
  /**
   * States and context.
   */
  private final RequestRunnerState prepareState;
  private final RequestRunnerState workState;
  private final RequestRunnerState endState;
  private final RequestRunnerState terminatingState = TerminatingState.v();
  private final RequestRunnerContext context;

  FractalImageRequestRunner(FractalImageRequest request, FileSystem fileSystem,
                            Provider<SimpleRgbFilmBuilder> simpleRgbFilmBuilderProvider,
                            Provider<IndependentSamplerBuilder> independentSamplerBuilderProvider) {
    this.request = request;
    this.fileSystem = fileSystem;
    this.simpleRgbFilmBuilderProvider = simpleRgbFilmBuilderProvider;
    this.independentSamplerBuilderProvider = independentSamplerBuilderProvider;

    // Initialize the states.
    prepareState = new PrepareState();
    workState = new WorkState();
    endState = new EndState();
    Map<String, RequestRunnerState> nameToStates =
      ImmutableList.of(prepareState, workState, endState, terminatingState)
        .stream()
        .collect(Collectors.toMap(RequestRunnerState::getName, Function.identity()));
    context = new ConcreteRunnerContext(this, nameToStates, prepareState);
  }

  @Override
  public Request getRequest() {
    return request;
  }

  @Override
  public void runMain(Main main) {
    context.runMain(main);
  }

  @Override
  public void runServer(Server server, String command, DataInputStream input, DataOutputStream output) {
    context.runServer(server, command, input, output);
  }

  @Override
  public String getDefaultOutputFileName(String inputFileName) {
    return FilenameUtils.removeExtension(inputFileName) + ".pfm";
  }

  class PrepareState implements RequestRunnerState {
    private static final String STATE_NAME = "PrepareState";
    private static final String PREPARE_COMMAND = STATE_NAME + ".prepare";

    @Override
    public String getName() {
      return STATE_NAME;
    }

    private void doPrepare(App app) {
      film = simpleRgbFilmBuilderProvider.get()
        .width(request.imageWidth().value())
        .height(request.imageHeight().value())
        .build();
      filmStorage = film.getExtension(FilmStorage.Vv.class).value();
      sampler = independentSamplerBuilderProvider.get()
        .sampleCount(request.sampleCount().value())
        .build();
      perPixelSampler = sampler.getExtension(PerPixelSampler.Vv.class).updatedValue();

      app.executeCommandOnAllRemoteServersInParallel(PREPARE_COMMAND, request.uuid(), true,
        (input, output) -> {
          // NO-OP
        });
    }


    @Override
    public void runMain(RequestRunnerContext context, Main main) {
      requestParameters = new BlockRendererParameters.Builder()
        .parse(main.getRequestParameters())
        .build();
      startMillis = System.currentTimeMillis();

      doPrepare(main);

      // Start the save timer.
      saveTimer = new Timer();
      if (!requestParameters.saveInterval().isAtTheEnd()) {
        saveTimer.scheduleAtFixedRate(
          new TimerTask() {
            @Override
            public void run() {
              filmStorage.save(main.getOutputFileName());
            }
          },
          requestParameters.saveInterval().getIntervalInSeconds() * 1000L,
          requestParameters.saveInterval().getIntervalInSeconds() * 1000L);
      }

      context.changeStateTo(workState, main);
    }

    @Override
    public void runServer(
      RequestRunnerContext context, Server server, String command, DataInputStream input, DataOutputStream output) {
      if (command.equals(PREPARE_COMMAND)) {
        try {
          doPrepare(server);
          output.writeUTF("success");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    @Override
    public boolean isTerminating() {
      return false;
    }
  }

  class WorkState extends WorkBlockRunnerState2D<FilmBlock> {
    private final double scale;
    private final Vector2d center;
    private final int imageSizeX;
    private final int imageSizeY;

    public WorkState() {
      super(request.uuid());
      this.scale = request.scale().value();
      this.center = new Vector2d(request.centerX().value(), request.centerY().value());
      this.imageSizeX = request.imageWidth().value();
      this.imageSizeY = request.imageHeight().value();
    }

    @Override
    public FilmBlock createBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
      return filmStorage.getFilmBlock(offsetX, offsetY, sizeX, sizeY);
    }

    @Override
    public Logger logger() {
      return logger;
    }

    @Override
    public RequestRunnerState nextState() {
      return endState;
    }

    private Rgb fractal(Vector2d p, Vector2d center, double scale) {
      Vector2d offset = VecMathDUtil.div(VecMathDUtil.sub(p, center), scale);
      double real = offset.x;
      double imag = offset.y;
      Vector2d c = new Vector2d(-0.4, 0.6);

      int j = 0;
      for (int i = 0; i < 100; i++) {
        double temp = (real * real) - (imag * imag) + c.x;
        imag = 2.0 * real * imag + c.y;
        real = temp;
        j = i + 1;

        Vector2d x = new Vector2d(real, imag);
        if (x.lengthSquared() > 4.0) {
          break;
        }
      }
      Rgb value = new Rgb(0.0, 0.0, 0.0);
      if (j == 100) {
        value.set(0.08, 0.10, 0.20);
      }
      return value;
    }

    @Override
    public void doWorkLocal(FilmBlock block) {
      PerPixelSampler sampler = perPixelSampler.duplicate();
      sampler.setSeed(Longs.toByteArray(System.currentTimeMillis() + System.identityHashCode(sampler)));

      int offsetX = block.getOffsetX();
      int offsetY = block.getOffsetY();
      int sizeX = block.getSizeX();
      int sizeY = block.getSizeY();
      Vector2d samplePosition = new Vector2d();
      /* For each pixel and pixel sample sample */
      int imageWidth = imageSizeX;
      int imageHeight = imageSizeY;
      for (int iy = 0; iy < sizeY; ++iy) {
        for (int ix = 0; ix < sizeX; ++ix) {
          sampler.generate();
          for (int i = 0; i < sampler.getSampleCount(); ++i) {
            sampler.next2D(samplePosition);
            double x = offsetX + ix + samplePosition.x;
            double y = offsetY + iy + samplePosition.y;
            RgbFilmRecorder recorder = (RgbFilmRecorder) block.getFilmRecorder(x, y);
            Vector2d p = new Vector2d(x / imageWidth, y / imageHeight);
            Rgb rgb = fractal(p, center, scale);
            recorder.record(rgb);
            block.put(recorder);
            sampler.advance();
          }
        }
      }
    }

    @Override
    public int generateAndDepositBlocks() {
      return WorkBlockRunnerState2D.generateAndDepositBlocks(this, new SimpleBlock2DGenerator(
        filmStorage.getSizeX(), filmStorage.getSizeY(), requestParameters.blockSize()));
    }

    @Override
    public void saveBlock(FilmBlock block) {
      filmStorage.put(block);
    }

    @Override
    public String getName() {
      return WorkState.class.getName();
    }
  }

  class EndState implements RequestRunnerState {

    @Override
    public String getName() {
      return EndState.class.getName();
    }

    @Override
    public void runMain(RequestRunnerContext context, Main main) {
      // Print the rendering time.
      long endMillis = System.currentTimeMillis();
      long elapsedMillis = endMillis - startMillis;
      logger.info(String.format("Rendering took %d min(s) %d second(s) %d ms",
        elapsedMillis / (60 * 1000), (elapsedMillis / 1000) % 60, elapsedMillis % 1000));
      if (requestParameters.writingTimingInfo()) {
        Path path = fileSystem.getPath(main.getOutputFileName() + "timing.txt");
        TimingInfoWriter.write(elapsedMillis, main.getArguments(), path);
      }

      saveTimer.cancel();
      saveTimer.purge();

      logger.info("Saving to " + main.getOutputFileName());
      filmStorage.save(main.getOutputFileName());

      context.changeStateTo(terminatingState, main);
    }

    @Override
    public void runServer(RequestRunnerContext context,
                          Server server,
                          String command,
                          DataInputStream input,
                          DataOutputStream output) {
      // NO-OP
    }

    @Override
    public boolean isTerminating() {
      return false;
    }
  }
}
