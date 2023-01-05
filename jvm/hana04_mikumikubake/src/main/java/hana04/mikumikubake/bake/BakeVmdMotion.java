package hana04.mikumikubake.bake;

import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.GdxNativesLoader;
import hana04.base.Module;
import hana04.base.filesystem.FileSystemModule;
import hana04.formats.mmd.generic.MmdAnimatedInstance;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.impl.ik.MikuMikuFlexIkSolver01;
import hana04.formats.mmd.generic.impl.physics.StandardMmdPhysics;
import hana04.formats.mmd.generic.impl.pmd.PmdModelAdaptor;
import hana04.formats.mmd.generic.impl.pmx.PmxModelAdaptor;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.vmd.VmdBoneKeyframe;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphKeyframe;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.shakuyaku.sbtm.SbtmAnimation;
import hana04.shakuyaku.sbtm.SbtmBonePose;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.inferred.freebuilder.FreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Optional;

public class BakeVmdMotion {
  private static final Logger logger = LoggerFactory.getLogger(BakeVmdMotion.class);

  private final FileSystem fileSystem;
  private Args args;
  private MmdModel mmdModel;
  private VmdMotion inVmd;
  private VmdMotion interVmd;
  private VmdMotion outVmd;

  public static void main(String[] args) {
    GdxNativesLoader.load();
    Bullet.init();
    Component component = DaggerBakeVmdMotion_Component.builder()
        .fileSystemModule(new FileSystemModule(FileSystems.getDefault()))
        .build();

    component.bakeVmdMotion().run(args);
  }

  @Singleton
  @dagger.Component(
      modules = {
          Module.class,
          hana04.serialize.Module.class,
          hana04.gfxbase.serialize.Module.class,
          hana04.shakuyaku.Module.class,
          hana04.mikumikubake.Module.class,
      }
  )
  public interface Component extends hana04.base.Component {
    FileSystem fileSystem();

    BakeVmdMotion bakeVmdMotion();
  }

  @Inject
  BakeVmdMotion(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void run(String[] commandLineArgs) {
    var optionalArgs = processCommandLineArguments(commandLineArgs);
    if (optionalArgs.isEmpty()) {
      return;
    }
    args = optionalArgs.get();
    loadModel();
    loadInVmd();
    prepareInterVmd();
    prepareOutVmd();
    compute();
    saveOutput();
  }

  private void loadModel() {
    try {
      String modelFileExtension = FilenameUtils.getExtension(args.modelFileName()).toLowerCase();
      if (modelFileExtension.equals("pmx")) {
        var pmxModel = PmxModel.load(fileSystem.getPath(args.modelFileName()));
        mmdModel = new PmxModelAdaptor(pmxModel);
      } else if (modelFileExtension.equals("pmd")) {
        var pmdModel = PmdModel.load(fileSystem.getPath(args.modelFileName()));
        mmdModel = new PmdModelAdaptor(pmdModel);
      } else {
        throw new RuntimeException("Unsupported model extension: " + modelFileExtension);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void loadInVmd() {
    try {
      inVmd = VmdMotion.load(args.vmdFileName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (getOutputStartFrame() > getOutputEndFrame()) {
      throw new RuntimeException("Start frame less than end frame");
    }
    if (getOutputStartFrame() > inVmd.getMaxFrame()) {
      throw new RuntimeException("Start frame not in range");
    }
    if (getOutputEndFrame() > inVmd.getMaxFrame()) {
      throw new RuntimeException("End frame not in range");
    }
  }

  private int getOutputStartFrame() {
    return args.outputStartFrame().orElse(0);
  }

  private int getOutputEndFrame() {
    return args.outputEndFrame().orElse(inVmd.getMaxFrame());
  }

  private void prepareInterVmd() {
    interVmd = new VmdMotion();
    int repeatCount = 1;
    if (args.repeat()) {
      repeatCount = 2;
    }
    interVmd.clearAndCopyMotionNames(inVmd);
    if (args.physicsInitializationFrameCount() > 0) {
      insertFirstFrameToInterVmd();
    }
    int inVmdLength = inVmd.getMaxFrame() + 1;
    for (int i = 0; i < repeatCount; i++) {
      copyFrames(inVmd, interVmd, args.physicsInitializationFrameCount() + i * inVmdLength);
    }
  }

  private void copyFrames(VmdMotion source, VmdMotion dest, int offset) {
    for (String boneName : source.boneMotions.keySet()) {
      VmdBoneMotion sourceMotion = source.boneMotions.get(boneName);
      VmdBoneMotion destMotion = dest.boneMotions.get(boneName);
      for (VmdBoneKeyframe sourceFrame : sourceMotion.keyFrames) {
        VmdBoneKeyframe destFrame = new VmdBoneKeyframe(sourceFrame);
        destFrame.frameNumber += offset;
        destMotion.keyFrames.add(destFrame);
      }
    }
    for (String morphName : source.morphMotions.keySet()) {
      VmdMorphMotion sourceMotion = source.morphMotions.get(morphName);
      VmdMorphMotion destMotion = dest.morphMotions.get(morphName);
      for (VmdMorphKeyframe sourceFrame : sourceMotion.keyFrames) {
        VmdMorphKeyframe destFrame = new VmdMorphKeyframe(sourceFrame);
        destFrame.frameNumber += offset;
        destMotion.keyFrames.add(destFrame);
      }
    }
  }


  private void insertFirstFrameToInterVmd() {
    for (String boneName : inVmd.boneMotions.keySet()) {
      VmdBoneMotion boneMotion = interVmd.boneMotions.get(boneName);
      VmdBoneKeyframe keyframe = new VmdBoneKeyframe();
      keyframe.frameNumber = 0;
      keyframe.displacement.set(0, 0, 0);
      keyframe.rotation.set(0, 0, 0, 1);
      boneMotion.keyFrames.add(0, keyframe);
    }
    for (String morphName : inVmd.morphMotions.keySet()) {
      VmdMorphMotion morphMotion = interVmd.morphMotions.get(morphName);
      morphMotion.keyFrames.add(0, new VmdMorphKeyframe());
      VmdMorphKeyframe keyframe = new VmdMorphKeyframe();
      keyframe.frameNumber = 0;
      keyframe.weight = 0;
      morphMotion.keyFrames.add(0, keyframe);
    }
  }

  private void prepareOutVmd() {
    outVmd = new VmdMotion();
    copyModelBoneAndMorphNameToVmdMotion(mmdModel, outVmd);
  }

  private void copyModelBoneAndMorphNameToVmdMotion(MmdModel mmdModel, VmdMotion vmdMotion) {
    mmdModel.bones().forEach(bone -> {
      VmdBoneMotion boneMotion = new VmdBoneMotion();
      boneMotion.boneName = bone.japaneseName();
      vmdMotion.boneMotions.put(bone.japaneseName(), boneMotion);
    });
    mmdModel.morphs().forEach(morph -> {
      VmdMorphMotion morphMotion = new VmdMorphMotion();
      morphMotion.morphName = morph.japaneseName();
      vmdMotion.morphMotions.put(morph.japaneseName(), morphMotion);
    });
  }

  private void compute() {
    int inVmdLength = inVmd.getMaxFrame() + 1;
    int sampleStart = args.physicsInitializationFrameCount() + getOutputStartFrame();
    if (args.repeat()) {
      sampleStart += inVmdLength;
    }
    int sampleEnd = sampleStart + getOutputEndFrame();
    if (args.loop()) {
      sampleEnd += 1;
    }

    var instance = new MmdAnimatedInstance(
        mmdModel,
        new MikuMikuFlexIkSolver01.Factory(),
        new StandardMmdPhysics.Factory());
    instance.setPhysicsEnabled(args.physicsEnabled());
    instance.getPhysics().setGravity(args.gravity());
    instance.getPhysics().setNumSubSteps(args.physicsSubstepCount());

    VpdPose inputPose = new VpdPose();
    VpdPose outputPose = new VpdPose();
    for (int frameIndex = 0; frameIndex <= sampleEnd; frameIndex++) {
      logger.info("frameIndex = " + frameIndex + " (" + String.format("%.2f", frameIndex * 100.0 / sampleEnd) + "%)");
      interVmd.getPose(frameIndex, inputPose);
      instance.setInputPost(inputPose);
      instance.update(args.timeFactor(), false);
      instance.getOutputPose(outputPose);
      if (frameIndex >= sampleStart && frameIndex <= sampleEnd) {
        int frameNumber = frameIndex - sampleStart;
        addKeyFrameToMotion(outVmd, outputPose, frameNumber);
      }
    }
    instance.dispose();
  }

  private static void addKeyFrameToMotion(VmdMotion vmdMotion, VpdPose vpdPose, int frameNumber) {
    Vector3f translation = new Vector3f();
    Quat4f rotation = new Quat4f();

    for (var boneName : vpdPose.boneNames()) {
      vpdPose.getBonePose(boneName, translation, rotation);
      if (TupleUtil.isNaN(translation)) {
        System.out.println("frameNumber = " + frameNumber + ", boneName = " + boneName + " [displacement]");
      }
      if (TupleUtil.isNaN(rotation)) {
        System.out.println("frameNumber = " + frameNumber + ", boneName = " + boneName + " [rotation]");
      }
      VmdBoneKeyframe keyframe = new VmdBoneKeyframe();
      keyframe.displacement.set(translation);
      keyframe.rotation.set(rotation);
      keyframe.frameNumber = frameNumber;
      VmdBoneMotion motion = vmdMotion.boneMotions.get(boneName);
      if (motion != null) {
        motion.keyFrames.add(keyframe);
      }
    }

    for (var morphName : vpdPose.morphNames()) {
      float weight = vpdPose.getMorphWeight(morphName);
      VmdMorphKeyframe keyframe = new VmdMorphKeyframe();
      keyframe.frameNumber = frameNumber;
      keyframe.weight = weight;
      VmdMorphMotion motion = vmdMotion.morphMotions.get(morphName);
      if (motion != null) {
        motion.keyFrames.add(keyframe);
      }
    }
  }

  private void saveOutput() {
    if (args.outputFormat().equals("vmd")) {
      // Write out the VMD motion.
      //outVmd.modelName = mmdModel.getJapaneseName();
      try {
        outVmd.save(args.outputFileName());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (args.outputFormat().equals("sbtma")) {
      saveStbmAnimation();
    }
  }

  private void saveStbmAnimation() {
    SbtmAnimation anim = new SbtmAnimation();
    anim.setRepeating(args.loop());

    double duration = 0;
    for (VmdBoneMotion boneMotion : outVmd.boneMotions.values()) {
      for (VmdBoneKeyframe keyframe : boneMotion.keyFrames) {
        duration = Math.max(duration, keyframe.frameNumber);
      }
    }
    for (VmdMorphMotion morphMotion : outVmd.morphMotions.values()) {
      for (VmdMorphKeyframe keyframe : morphMotion.keyFrames) {
        duration = Math.max(duration, keyframe.frameNumber);
      }
    }
    anim.setDuration(duration);

    for (VmdBoneMotion boneMotion : outVmd.boneMotions.values()) {
      for (VmdBoneKeyframe keyframe : boneMotion.keyFrames) {
        SbtmBonePose bonePose = new SbtmBonePose();
        bonePose.translation.set(keyframe.displacement.x, keyframe.displacement.y, keyframe.displacement.z);
        bonePose.rotation.set(keyframe.rotation.x, keyframe.rotation.y, keyframe.rotation.z, keyframe.rotation.w);
        anim.putBoneKeyFrame(boneMotion.boneName, keyframe.frameNumber, bonePose);
      }
    }

    for (VmdMorphMotion morphMotion : outVmd.morphMotions.values()) {
      for (VmdMorphKeyframe keyframe : morphMotion.keyFrames) {
        anim.putMorphKeyFrame(morphMotion.morphName, keyframe.frameNumber, keyframe.weight);
      }
    }

    anim.saveBinary(fileSystem.getPath(args.outputFileName()));
  }


  private Optional<Args> processCommandLineArguments(String[] commandLineArgs) {
    CommandLineParser parser = new DefaultParser();
    var options = createOptions();
    Args.Builder args = Args.builder();
    try {
      CommandLine cmd = parser.parse(options, commandLineArgs);
      String[] parsedArgs = cmd.getArgs();
      if (parsedArgs.length < 3) {
        displayHelp(options);
        return Optional.empty();
      }

      args.vmdFileName(parsedArgs[0]);
      args.modelFileName(parsedArgs[1]);
      args.outputFileName(parsedArgs[2]);


      if (cmd.hasOption("h")) {
        displayHelp(options);
        return Optional.empty();
      }
      if (cmd.hasOption("r")) {
        args.repeat(true);
      }
      if (cmd.hasOption("i")) {
        args.physicsInitializationFrameCount(Integer.parseInt(cmd.getOptionValue("i")));
      }
      if (cmd.hasOption("g")) {
        Vector3f gravity = new Vector3f();
        String[] comps = cmd.getOptionValue("g").split(",");
        gravity.x = Float.parseFloat(comps[0]);
        gravity.y = Float.parseFloat(comps[1]);
        gravity.z = Float.parseFloat(comps[2]);
        args.gravity(gravity);
      }
      if (cmd.hasOption("t")) {
        float timeFactor = Float.parseFloat(cmd.getOptionValue("t")) / 30.0f;
        args.timeFactor(timeFactor);
      }
      if (cmd.hasOption("s")) {
        int physicsSubstepCount = Integer.parseInt(cmd.getOptionValue("s"));
        args.physicsSubstepCount(physicsSubstepCount);
      }
      if (cmd.hasOption("d")) {
        String[] comps = cmd.getOptionValue("d").split(",");
        Vector3f worldDisplacement = new Vector3f();
        worldDisplacement.x = Float.parseFloat(comps[0]);
        worldDisplacement.y = Float.parseFloat(comps[1]);
        worldDisplacement.z = Float.parseFloat(comps[2]);
        args.worldDisplacement(worldDisplacement);
      }
      if (cmd.hasOption("f")) {
        String outputFormat = cmd.getOptionValue("f").toLowerCase();
        args.outputFormat(outputFormat);
      } else {
        args.outputFormat(FilenameUtils.getExtension(args.outputFileName()).toLowerCase());
      }
      if (cmd.hasOption("l")) {
        args.loop(true);
      }
      if (cmd.hasOption("a")) {
        args.outputStartFrame(Integer.parseInt(cmd.getOptionValue("a")));
      }
      if (cmd.hasOption("z")) {
        args.outputEndFrame(Integer.parseInt(cmd.getOptionValue("z")));
      }
      if (cmd.hasOption("T")) {
        args.removeTranslation(true);
      }
      if (cmd.hasOption("p")) {
        args.physicsEnabled(Boolean.parseBoolean(cmd.getOptionValue("p")));
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    if (!args.outputFormat().equals("vmd") && !args.outputFormat().equals("sbtma")) {
      throw new RuntimeException("Format '" + args.outputFormat() + "' is unsupported");
    }

    return Optional.of(args.build());
  }

  private static Options createOptions() {
    var options = new Options();
    options.addOption("h", "help", false, "display help");
    options.addOption("r", "repeat", false, "repeat the motion 3 times and take the middle (default=false)");
    options.addOption("l", "loop", false, "insert first frame to the last frame to make looping skeleton");
    options.addOption("i", "initial-frame", true, "number of initial frames to initialize physical simulation " +
        "(default=0)");
    options.addOption("g", "gravity", true, "gravity vector (default=(0,-98,0))");
    options.addOption("t", "time-scale", true, "factor to scale time when feeding to physical simulation (default=1)");
    options.addOption("s", "substep", true, "how many substeps to divide each frame when computing the skeleton " +
        "(default=10)");
    options.addOption("f", "output-format", true, "the output file format (vmd, sbtma)");
    options.addOption("a", "start-frame", true, "the first frame to output");
    options.addOption("z", "end-frame", true, "the last frame to output");
    options.addOption("T", "remove-translation", false, "remove translation");
    options.addOption("d", "displacement", true, "world-space displacement to add to the skeleton (default=(0,0,0))");
    options.addOption("p", "use-physics", true, "Use physical simulation (default=true)");
    return options;
  }

  private static void displayHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        "java " + BakeVmdMotion.class.getCanonicalName() + " <vmd-file> <model-file> <output-file>",
        options);
  }

  @FreeBuilder
  public interface Args {
    String vmdFileName();

    String modelFileName();

    String outputFileName();

    boolean repeat();

    boolean loop();

    int physicsSubstepCount();

    float timeFactor();

    int physicsInitializationFrameCount();

    Vector3f gravity();

    String outputFormat();

    Optional<Integer> outputStartFrame();

    Optional<Integer> outputEndFrame();

    boolean removeTranslation();

    Vector3f worldDisplacement();

    boolean physicsEnabled();

    class Builder extends BakeVmdMotion_Args_Builder {
      Builder() {
        super();
        gravity(new Vector3f(0, -9.8f * 10, 0));
        repeat(false);
        loop(false);
        physicsInitializationFrameCount(0);
        timeFactor(1.0f / 30);
        physicsSubstepCount(10);
        worldDisplacement(new Vector3f(0, 0, 0));
        physicsEnabled(true);
        removeTranslation(false);
      }
    }

    static Builder builder() {
      return new Builder();
    }
  }
}