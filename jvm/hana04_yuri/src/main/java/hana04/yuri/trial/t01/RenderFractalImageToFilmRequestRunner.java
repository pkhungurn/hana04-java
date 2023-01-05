package hana04.yuri.trial.t01;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import hana04.base.util.TypeUtil;
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
import hana04.yuri.film.Film;
import hana04.yuri.film.FilmStorage;
import hana04.yuri.film.interfaces.CanRecordRgbFilmBlock;
import hana04.yuri.film.interfaces.CanRecordRgbFilmStorage;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.request.params.BlockRendererParameters;
import hana04.yuri.sampler.PerPixelSampler;
import hana04.yuri.sampler.Sampler;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class RenderFractalImageToFilmRequestRunner implements Request.Runner {
  private static Logger logger = LoggerFactory.getLogger(RenderFractalImageToFilmRequest.class);
  /**
   * Assigned film.
   */
  private final RenderFractalImageToFilmRequest request;
  private final FileSystem fileSystem;
  /**
   * States and context.
   */
  private final RequestRunnerState prepareState;
  private final RequestRunnerState workState;
  private final RequestRunnerState endState;
  private final RequestRunnerState terminatingState = TerminatingState.v();
  private final RequestRunnerContext context;
  /**
   * Computed fields.
   */
  private BlockRendererParameters requestParameters;
  private long startMillis;
  private Timer saveTimer;
  private Film film;
  private CanRecordRgbFilmStorage filmStorage;
  private Sampler sampler;
  private PerPixelSampler perPixelSampler;

  RenderFractalImageToFilmRequestRunner(RenderFractalImageToFilmRequest request,
                                        FileSystem fileSystem) {
    this.request = request;
    this.fileSystem = fileSystem;

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
      film = request.film().updatedValue();
      sampler = request.sampler().updatedValue();
      filmStorage = TypeUtil.cast(
        film.getExtension(FilmStorage.Vv.class).updatedValue(),
        CanRecordRgbFilmStorage.class);
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
    public void runServer(RequestRunnerContext context,
                          Server server,
                          String command,
                          DataInputStream input,
                          DataOutputStream output) {
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

  class WorkState extends WorkBlockRunnerState2D<CanRecordRgbFilmBlock> {
    private final Vector2d center;
    private final double scale;

    WorkState() {
      super(request.uuid());
      this.scale = request.scale().value();
      this.center = new Vector2d(request.centerX().value(), request.centerY().value());
    }

    @Override
    public CanRecordRgbFilmBlock createBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
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

    @Override
    public void doWorkLocal(CanRecordRgbFilmBlock block) {
      int imageSizeX = film.width().value();
      int imageSizeY = film.height().value();

      PerPixelSampler perPixelSampler = RenderFractalImageToFilmRequestRunner.this.perPixelSampler.duplicate();
      perPixelSampler.setSeed(Longs.toByteArray(System.currentTimeMillis()
        + System.identityHashCode(perPixelSampler)));

      int offsetX = block.getOffsetX();
      int offsetY = block.getOffsetY();
      int sizeX = block.getSizeX();
      int sizeY = block.getSizeY();
      Vector2d samplePosition = new Vector2d();
      /* For each pixel and pixel sample sample */
      for (int iy = 0; iy < sizeY; ++iy) {
        for (int ix = 0; ix < sizeX; ++ix) {
          perPixelSampler.generate();
          for (int i = 0; i < perPixelSampler.getSampleCount(); ++i) {
            perPixelSampler.next2D(samplePosition);
            double x = offsetX + ix + samplePosition.x;
            double y = offsetY + iy + samplePosition.y;
            RgbFilmRecorder recorder = (RgbFilmRecorder) block.getFilmRecorder(x, y);
            Vector2d p = new Vector2d(x / imageSizeX, y / imageSizeY);
            Rgb rgb = fractal(p);
            recorder.record(rgb);
            block.put(recorder);
            perPixelSampler.advance();
          }
        }
      }
    }

    private Rgb fractal(Vector2d p) {
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
    public int generateAndDepositBlocks() {
      return WorkBlockRunnerState2D.generateAndDepositBlocks(this, new SimpleBlock2DGenerator(
        filmStorage.getSizeX(), filmStorage.getSizeY(), requestParameters.blockSize()));
    }

    @Override
    public void saveBlock(CanRecordRgbFilmBlock block) {
      filmStorage.put(block);
    }

    @Override
    public String getName() {
      return "WorkState";
    }
  }

  class EndState implements RequestRunnerState {
    @Override
    public String getName() {
      return "EndState";
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
