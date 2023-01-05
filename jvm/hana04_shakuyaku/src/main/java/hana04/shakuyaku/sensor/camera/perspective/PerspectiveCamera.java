package hana04.shakuyaku.sensor.camera.perspective;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.sensor.camera.Camera;

@HanaDeclareObject(
  parent = Camera.class,
  typeId = TypeIds.TYPE_ID_PERSPECTIVE_CAMERA,
  typeNames = {"shakuyaku.PerspectiveCamera", "PerspectiveCamera"})
public interface PerspectiveCamera extends Camera {
  /**
   * The camera-to-world transform.
   */
  @HanaProperty(1)
  Variable<Transform> toWorld();

  /**
   * The aspect ratio = width/height of the image place.
   */
  @HanaProperty(2)
  Variable<Double> aspect();

  /**
   * The vertical field of view in degrees.
   */
  @HanaProperty(3)
  Variable<Double> fovY();

  /**
   * The near clipping plane location in world-space unit.
   */
  @HanaProperty(4)
  Variable<Double> nearClip();

  /**
   * The far clipping plane location in world-space unit.
   */
  @HanaProperty(5)
  Variable<Double> farClip();

  /**
   * Crop window specification.
   */
  @HanaProperty(6)
  Variable<Double> cropOffsetX();

  @HanaProperty(7)
  Variable<Double> cropOffsetY();

  @HanaProperty(8)
  Variable<Double> cropSizeX();

  @HanaProperty(9)
  Variable<Double> cropSizeY();
}