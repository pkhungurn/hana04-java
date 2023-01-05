package hana04.mikumikubake.modelview;

import hana04.formats.mmd.vmd.VmdMotion;
import hana04.formats.mmd.vpd.VpdPose;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class MmdInputPoseManager {
  private static Logger logger = LoggerFactory.getLogger(MmdModelViewer.class);
  private Optional<VmdMotion> loadedVmdMotion;
  private Optional<VpdPose> loadedVpdPose;
  private VpdPose currentPose;
  private double currentFrame = 0;
  private double animationLength = 0;
  private String absolutePath = "";

  @Inject
  public MmdInputPoseManager() {
    loadedVmdMotion = Optional.empty();
    loadedVpdPose = Optional.empty();
    currentPose = new VpdPose();
  }

  public boolean hasPose() {
    return loadedVpdPose.isPresent() || loadedVmdMotion.isPresent();
  }

  public void load(String absolutePath) {
    String extension = FilenameUtils.getExtension(absolutePath);
    if (extension.equals("vmd")) {
      try {
        loadedVmdMotion = Optional.of(VmdMotion.load(absolutePath));
        loadedVpdPose = Optional.empty();
        this.absolutePath = absolutePath;
      } catch (Exception e) {
        loadedVmdMotion = Optional.empty();
        this.absolutePath = "";
        logger.error("Could not load " + absolutePath);
      }
    } else if (extension.equals("vpd")) {
      try {
        loadedVmdMotion = Optional.empty();
        loadedVpdPose = Optional.of(VpdPose.load(absolutePath));
        this.absolutePath = absolutePath;
      } catch (Exception e) {
        loadedVpdPose = Optional.empty();
        this.absolutePath = "";logger.error("Could not load " + absolutePath);

      }
    } else {
      logger.info("File of extension " + extension + " is not supports!");
    }

    if (loadedVpdPose.isPresent()) {
      animationLength = 0;
      currentFrame = 0;
    } else if (loadedVmdMotion.isPresent()) {
      animationLength = loadedVmdMotion.get().getMaxFrame();
      currentFrame = 0;
    }
    updatePose();
  }

  public void clear() {
    loadedVmdMotion = Optional.empty();
    loadedVmdMotion = Optional.empty();
    animationLength = 0;
    currentFrame = 0;
    currentPose.clear();
    absolutePath = "";
  }


  public VpdPose getCurrentPose() {
    return currentPose;
  }

  public void updatePose() {
    loadedVpdPose.ifPresent(vpdPose -> currentPose.copy(vpdPose));
    loadedVmdMotion.ifPresent(vmdMotion -> {
      vmdMotion.getPose((float) currentFrame, currentPose);
    });
  }

  public void setFrame(double newFrame) {
    currentFrame = newFrame;
    updatePose();
  }

  public double getCurrentFrame() {
    return currentFrame;
  }

  public double getAnimationLength() {
    return animationLength;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }
}
