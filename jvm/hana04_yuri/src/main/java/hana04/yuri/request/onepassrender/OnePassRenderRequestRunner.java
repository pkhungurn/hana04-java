package hana04.yuri.request.onepassrender;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import hana04.base.caching.HanaUnwrapper;
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
import hana04.yuri.film.Film;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.FilmStorage;
import hana04.yuri.integrand.ScenePreparer;
import hana04.yuri.integrand.SensorIntegrand;
import hana04.yuri.integrand.SensorIntegrandEvaluator;
import hana04.yuri.request.params.BlockRendererParameters;
import hana04.yuri.sampler.PerPixelSampler;
import hana04.yuri.sampler.Sampler;
import hana04.shakuyaku.scene.Scene;
import hana04.yuri.sensor.SensorRay;
import hana04.shakuyaku.sensor.camera.Camera;
import hana04.yuri.sensor.camera.CameraRayGenerator;
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

public class OnePassRenderRequestRunner implements Request.Runner {
  private static Logger logger = LoggerFactory.getLogger(OnePassRenderRequestRunner.class);
  /**
   * Assigned fields.
   */
  private final OnePassRenderRequest request;
  private final HanaUnwrapper unwrapper;
  private final FileSystem fileSystem;
  /**
   * Computed fields.
   */
  private Film film;
  private Camera camera;
  private Sampler sampler;
  private SensorIntegrand integrand;
  private Scene scene;
  private BlockRendererParameters requestParameters;
  /**
   * Extensions
   */
  private FilmStorage filmStorage;
  private CameraRayGenerator cameraRayGenerator;
  private PerPixelSampler perPixelSampler;
  private SensorIntegrandEvaluator integrandEvaluator;
  private long startMillis;
  private Timer saveTimer;
  /**
   * States and context.
   */
  private final RequestRunnerState prepareState;
  private final RequestRunnerState workState;
  private final RequestRunnerState endState;
  private final RequestRunnerState terminatingState = TerminatingState.v();
  private final RequestRunnerContext context;

  OnePassRenderRequestRunner(OnePassRenderRequest request, HanaUnwrapper unwrapper, FileSystem fileSystem) {
    this.request = request;
    this.unwrapper = unwrapper;
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
      camera = request.camera().updatedValue().unwrap(unwrapper);
      sampler = request.sampler().updatedValue();
      integrand = request.integrand().updatedValue();
      scene = request.scene().updatedValue().unwrap(unwrapper);

      filmStorage = film.getExtension(FilmStorage.Vv.class).updatedValue();
      perPixelSampler = sampler.getExtension(PerPixelSampler.Vv.class).updatedValue();
      cameraRayGenerator = camera.getExtension(CameraRayGenerator.Vv.class).updatedValue();
      integrandEvaluator = integrand.getExtension(SensorIntegrandEvaluator.Vv.class).updatedValue();

      ScenePreparer scenePreparer = integrand.getExtension(ScenePreparer.Vv.class).updatedValue();
      scenePreparer.prepare(scene);

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
    public void runServer(RequestRunnerContext context, Server server, String command, DataInputStream input,
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

  class WorkState extends WorkBlockRunnerState2D<FilmBlock> {
    WorkState() {
      super(request.uuid());
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

    @Override
    public void doWorkLocal(FilmBlock block) {
      int imageSizeX = film.width().value();
      int imageSizeY = film.height().value();

      PerPixelSampler perPixelSampler = OnePassRenderRequestRunner.this.perPixelSampler.duplicate();
      perPixelSampler.setSeed(Longs.toByteArray(System.currentTimeMillis()
        + System.identityHashCode(perPixelSampler)));

      int offsetX = block.getOffsetX();
      int offsetY = block.getOffsetY();
      int sizeX = block.getSizeX();
      int sizeY = block.getSizeY();
      Vector2d mu0 = new Vector2d();
      Vector2d imagePlanePosition = new Vector2d();
      Vector2d aperturePosition = new Vector2d();
      for (int iy = 0; iy < sizeY; ++iy) {
        for (int ix = 0; ix < sizeX; ++ix) {
          perPixelSampler.generate();
          for (int i = 0; i < perPixelSampler.getSampleCount(); ++i) {
            perPixelSampler.next2D(mu0);
            imagePlanePosition.set((ix + offsetX + mu0.x) / imageSizeX, (iy + offsetY + mu0.y) / imageSizeY);
            perPixelSampler.next2D(aperturePosition);

            SensorRay sensorRay = cameraRayGenerator.generate(imagePlanePosition, aperturePosition);
            FilmRecorder recorder = block.getFilmRecorder(
              imagePlanePosition.x * imageSizeX,
              imagePlanePosition.y * imageSizeY);
            integrandEvaluator.eval(scene, sensorRay.ray, perPixelSampler, recorder);
            recorder.scale(sensorRay.importanceWeight);

            block.put(recorder);
            perPixelSampler.advance();
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
