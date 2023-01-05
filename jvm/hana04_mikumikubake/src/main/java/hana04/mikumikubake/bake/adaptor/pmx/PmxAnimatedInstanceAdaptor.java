package hana04.mikumikubake.bake.adaptor.pmx;

import hana04.formats.mmd.pmx.PmxAnimatedInstance;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxPose;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.mikumikubake.bake.adaptor.MmdAnimatedInstanceAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

public class PmxAnimatedInstanceAdaptor implements MmdAnimatedInstanceAdaptor<PmxModel, PmxPose> {
  private final PmxModel pmxModel;
  private final PmxAnimatedInstance instance;

  public PmxAnimatedInstanceAdaptor(PmxModel pmxModel) {
    this.pmxModel = pmxModel;
    this.instance = new PmxAnimatedInstance(pmxModel);
  }

  @Override
  public void setGravity(float x, float y, float z) {
    instance.getPhysics().setGravity(x, y, z);
  }

  @Override
  public void enablePhysics(boolean enabled) {
    instance.enablePhysics(enabled);
  }

  @Override
  public void resetPhysics(MmdPoseAdaptor<PmxPose> modelPose) {
    instance.resetPhysics(modelPose.getPose());
  }

  @Override
  public void getModelPose(MmdPoseAdaptor<PmxPose> modelPose) {
    instance.getPmxPose(modelPose.getPose());
  }

  @Override
  public void setModelPose(MmdPoseAdaptor<PmxPose> modelPose) {
    instance.setPmxPose(modelPose.getPose());
  }

  @Override
  public void setVpdPose(VpdPose vpdPose) {
    instance.setVpdPose(vpdPose);
  }

  @Override
  public void update(float elapsedTime) {
    instance.update(elapsedTime);
  }

  @Override
  public void dispose() {
    instance.dispose();
  }
}
