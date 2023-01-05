package hana04.mikumikubake.opengl.renderer00.camera.ui;

import com.google.common.collect.ImmutableList;
import hana04.mikumikubake.opengl.renderer00.camera.PerspectiveProjection;

public class PerspectiveProjectionControl extends AbstractProjectionControl implements CameraProjectionControl<PerspectiveProjection> {
  private final PerspectiveProjection perspectiveProjection;

  PerspectiveProjectionControl(PerspectiveProjection perspectiveProjection) {
    super(
      ImmutableList.of(
        Parameter.newBuilder().label("Near:").boxedValue(perspectiveProjection.boxedNear).build(),
        Parameter.newBuilder().label("Far:").boxedValue(perspectiveProjection.boxedFar).build(),
        Parameter.newBuilder().label("Field-of-view Y").boxedValue(perspectiveProjection.boxedFovY).build()));
    this.perspectiveProjection = perspectiveProjection;
  }

  @Override
  public PerspectiveProjection getProjection() {
    return perspectiveProjection;
  }
}