package hana04.mikumikubake.bake.adaptor.pmd;

import hana04.formats.mmd.pmd.PmdAnimatedInstance;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmd.PmdPose;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.mikumikubake.bake.adaptor.MmdAnimatedInstanceAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

public class PmdAnimatedInstanceAdaptor implements MmdAnimatedInstanceAdaptor<PmdModel, PmdPose> {
  private final PmdModel model;
  private final PmdAnimatedInstance instance;

  public PmdAnimatedInstanceAdaptor(PmdModel model) {
    this.model = model;
    this.instance = new PmdAnimatedInstance(model);
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
  public void resetPhysics(MmdPoseAdaptor<PmdPose> modelPose) {
    instance.resetPhysics(modelPose.getPose());
  }

  @Override
  public void getModelPose(MmdPoseAdaptor<PmdPose> modelPose) {
    instance.getPmdPose(modelPose.getPose());
  }

  @Override
  public void setModelPose(MmdPoseAdaptor<PmdPose> modelPose) {
    instance.setPmdPose(modelPose.getPose());
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
