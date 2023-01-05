package hana04.shakuyaku.sbtm;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class SbtmBonePose {
  public final Vector3d translation = new Vector3d();
  public final Quat4d rotation = new Quat4d();

  public SbtmBonePose() {
    translation.set(0, 0, 0);
    rotation.set(0, 0, 0, 1);
  }

  public SbtmBonePose(Vector3d translation, Quat4d rotation) {
    this.translation.set(translation);
    this.rotation.set(rotation);
  }

  public SbtmBonePose(SbtmBonePose other) {
    this.set(other);
  }

  public void set(SbtmBonePose other) {
    this.translation.set(other.translation);
    this.rotation.set(other.rotation);
  }

  public void clear() {
    this.translation.set(0, 0, 0);
    this.rotation.set(0, 0, 0, 1);
  }
}
