package hana04.mikumikubake.bake;

import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.vmd.VmdBoneKeyframe;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphKeyframe;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.mikumikubake.bake.adaptor.MmdAnimatedInstanceAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdModelAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;
import hana04.mikumikubake.bake.adaptor.pmd.PmdModelAdaptor;
import hana04.mikumikubake.bake.adaptor.pmx.PmxModelAdaptor;
import hana04.shakuyaku.sbtm.SbtmAnimation;
import hana04.shakuyaku.sbtm.SbtmBonePose;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Vector3f;
import java.nio.file.FileSystems;

public class BakeVmdMotionLegacy {
  private static Logger logger = LoggerFactory.getLogger(BakeVmdMotionLegacy.class);
  // Input
  private String vmdFileName;
  private String modelFileName;
  private String outputFileName;
  private boolean repeat = false;
  private boolean loop = false;
  private int substepCount = 10;
  private float timeFactor = 1.0f / 30;
  private int initialFrameCount = 0;
  private Vector3f gravity = new Vector3f(0, -98, 0);
  private String outputFormat;
  private int outputStartFrame;
  private int outputEndFrame;
  private boolean removeTranslation = false;
  private Vector3f worldDisplacement = new Vector3f(0, 0, 0);
  private boolean physicsEnabled = true;

  // Program controls.
  private boolean shouldExitNow = false;
  private Options options;

  // States
  MmdModelAdaptor<?, ?> model;
  VmdMotion inVmd, interVmd, outVmd;


  public static void main(String[] args) {
    new BakeVmdMotionLegacy().run(args);
  }

  private void run(String[] args) {
    initializeOptions();

    processCommandLineArguments(args);
    if (shouldExitNow) {
      return;
    }

    loadModel();
    loadInVmd();
    prepareInterVmd();
    prepareOutVmd();
    compute();
    saveOutput();
  }

  private void initializeOptions() {
    options = new Options();
    options.addOption("h", "help", false, "display help");
    options.addOption("r", "repeat", false, "repeat the motion 3 times and take the middle (default=false)");
    options.addOption("l", "loop", false, "insert first frame to the last frame to make looping skeleton");
    options.addOption("i", "initial-frame", true, "number of initial frames to initialize physical simulation " +
      "(default=0)");
    options.addOption("g", "gravity", true, "gravity vector (default=(0,-100,0))");
    options.addOption("t", "time-scale", true, "factor to scale time when feeding to physical simulation (default=1)");
    options.addOption("s", "substep", true, "how many substeps to divide each frame when computing the skeleton " +
      "(default=10)");
    options.addOption("f", "output-format", true, "the output file format (vmd, sbtma)");
    options.addOption("a", "start-frame", true, "the first frame to output");
    options.addOption("z", "end-frame", true, "the last frame to output");
    options.addOption("T", "remove-translation", false, "remove translation");
    options.addOption("d", "displacement", true, "world-space displacement to add to the skeleton (default=(0,0,0))");
    options.addOption("p", "use-physics", true, "Use physical simulation (default=true)");
  }

  private void processCommandLineArguments(String[] args) {
    CommandLineParser parser = new PosixParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      String[] parsedArgs = cmd.getArgs();
      if (parsedArgs.length < 3) {
        displayHelp(options);
        shouldExitNow = true;
        return;
      }

      vmdFileName = parsedArgs[0];
      modelFileName = parsedArgs[1];
      outputFileName = parsedArgs[2];

      if (cmd.hasOption("h")) {
        displayHelp(options);
        shouldExitNow = true;
        return;
      }
      if (cmd.hasOption("r")) {
        repeat = true;
      }
      if (cmd.hasOption("i")) {
        initialFrameCount = Integer.parseInt(cmd.getOptionValue("i"));
      }
      if (cmd.hasOption("g")) {
        String[] comps = cmd.getOptionValue("g").split(",");
        gravity.x = Float.parseFloat(comps[0]);
        gravity.y = Float.parseFloat(comps[1]);
        gravity.z = Float.parseFloat(comps[2]);
      }
      if (cmd.hasOption("t")) {
        timeFactor = timeFactor * Float.parseFloat(cmd.getOptionValue("t"));
      }
      if (cmd.hasOption("s")) {
        substepCount = Integer.parseInt(cmd.getOptionValue("s"));
      }
      if (cmd.hasOption("d")) {
        String[] comps = cmd.getOptionValue("d").split(",");
        worldDisplacement.x = Float.parseFloat(comps[0]);
        worldDisplacement.y = Float.parseFloat(comps[1]);
        worldDisplacement.z = Float.parseFloat(comps[2]);
      }
      if (cmd.hasOption("f")) {
        outputFormat = cmd.getOptionValue("f").toLowerCase();
      } else {
        outputFormat = FilenameUtils.getExtension(outputFileName).toLowerCase();
      }

      if (cmd.hasOption("l")) {
        loop = true;
      }

      if (cmd.hasOption("a")) {
        outputStartFrame = Integer.parseInt(cmd.getOptionValue("a"));
      } else {
        outputStartFrame = Integer.MIN_VALUE;
      }

      if (cmd.hasOption("z")) {
        outputEndFrame = Integer.parseInt(cmd.getOptionValue("z"));
      } else {
        outputEndFrame = Integer.MIN_VALUE;
      }

      if (cmd.hasOption("T")) {
        removeTranslation = true;
      }

      if (cmd.hasOption("p")) {
        physicsEnabled = Boolean.parseBoolean(cmd.getOptionValue("p"));
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    if (!outputFormat.equals("vmd") && !outputFormat.equals("sbtma")) {
      throw new RuntimeException("Format '" + outputFormat + "' is unsupported");
    }
  }

  private void displayHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java " + BakeVmdMotionLegacy.class.getCanonicalName() + " <vmd-file> <pmd-file> <output-file>",
      options);
  }

  private void loadModel() {
    try {
      String modelFileExtension = FilenameUtils.getExtension(modelFileName).toLowerCase();
      if (modelFileExtension.equals("pmx")) {
        model = new PmxModelAdaptor(PmxModel.load(modelFileName));
      } else if (modelFileExtension.equals("pmd")) {
        model = new PmdModelAdaptor(PmdModel.load(modelFileName));
      } else {
        throw new RuntimeException("Unsupported model extension: " + modelFileExtension);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void loadInVmd() {
    try {
      inVmd = VmdMotion.load(vmdFileName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (outputStartFrame == Integer.MIN_VALUE) {
      outputStartFrame = 0;
    }
    if (outputEndFrame == Integer.MIN_VALUE) {
      outputEndFrame = inVmd.getMaxFrame();
    }
    if (outputStartFrame > outputEndFrame) {
      throw new RuntimeException("Start frame less than end frame");
    }
    if (outputStartFrame > inVmd.getMaxFrame()) {
      throw new RuntimeException("Start frame not in range");
    }
    if (outputEndFrame > inVmd.getMaxFrame()) {
      throw new RuntimeException("End frame not in range");
    }
  }

  private void prepareInterVmd() {
    interVmd = new VmdMotion();
    int repeatCount = 1;
    if (repeat)
      repeatCount = 2;
    prepareVmdToInVmd(interVmd);
    if (initialFrameCount > 0) {
      insertFirstFrameToInterVmd();
    }
    int inVmdLength = inVmd.getMaxFrame() + 1;
    for (int i = 0; i < repeatCount; i++) {
      copyFrames(inVmd, interVmd, initialFrameCount + i * inVmdLength);
    }
  }

  private void prepareVmdToInVmd(VmdMotion vmd) {
    for (String boneName : inVmd.boneMotions.keySet()) {
      VmdBoneMotion boneMotion = new VmdBoneMotion();
      boneMotion.boneName = boneName;
      vmd.boneMotions.put(boneName, boneMotion);
    }
    for (String morphName : inVmd.morphMotions.keySet()) {
      VmdMorphMotion morphMotion = new VmdMorphMotion();
      morphMotion.morphName = morphName;
      vmd.morphMotions.put(morphName, morphMotion);
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

  private void prepareOutVmd() {
    outVmd = new VmdMotion();
    model.populateBoneAndMorphNames(outVmd);
  }

  private void compute() {
    int inVmdLength = inVmd.getMaxFrame() + 1;
    int sampleStart = initialFrameCount + outputStartFrame;
    if (repeat) {
      sampleStart += inVmdLength;
    }
    int sampleEnd = sampleStart + outputEndFrame;

    // Play the skeleton and save it to the new skeleton.
    MmdPoseAdaptor inputPose = model.createPose();
    MmdPoseAdaptor outputPose = model.createPose();

    MmdAnimatedInstanceAdaptor instance = model.createAnimatedInstance();
    if (physicsEnabled) {
      instance.enablePhysics(false);
      inputPose.copyMotionFrame(interVmd, 0);
      instance.resetPhysics(inputPose);
      instance.setGravity(gravity.x, gravity.y, gravity.z);
      instance.enablePhysics(true);
    } else {
      instance.enablePhysics(false);
    }

    if (loop) {
      sampleEnd += 1;
    }
    for (int frameIndex = 0; frameIndex <= sampleEnd; frameIndex++) {
      logger.info("frameIndex = " + frameIndex + " (" + String.format("%.2f", frameIndex * 100.0 / sampleEnd) + "%)");
      for (int i = 0; i < substepCount; i++) {
        inputPose.copyMotionFrame(interVmd, frameIndex + i * 1.0f / substepCount);
        instance.setModelPose(inputPose);
        instance.update(timeFactor / substepCount);
      }
      instance.getModelPose(outputPose);

      if (frameIndex >= sampleStart && frameIndex <= sampleEnd) {
        int frameNumber = frameIndex - sampleStart;
        outputPose.addKeyFrameToMotion(outVmd, frameNumber);
      }
    }
    instance.dispose();
  }

  private void saveOutput() {
    if (outputFormat.equals("vmd")) {
      // Write out the VMD motion.
      outVmd.modelName = model.getJapaneseName();
      try {
        outVmd.save(outputFileName);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (outputFormat.equals("sbtma")) {
      saveStbmAnimation();
    }
  }

  private void saveStbmAnimation() {
    SbtmAnimation anim = new SbtmAnimation();
    anim.setRepeating(loop);

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

    anim.saveBinary(FileSystems.getDefault().getPath(outputFileName));
  }
}
