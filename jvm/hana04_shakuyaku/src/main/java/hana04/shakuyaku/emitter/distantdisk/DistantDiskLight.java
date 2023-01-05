package hana04.shakuyaku.emitter.distantdisk;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;

@HanaDeclareObject(
  parent = Emitter.class,
  typeId = TypeIds.TYPE_ID_DISTANT_DISK_LIGHT,
  typeNames = {"shakuyaku.DistantDiskLight", "DistantDiskLight"})
public interface DistantDiskLight extends Emitter {
  /**
   * The per-direction radiance of the light source.
   */
  @HanaProperty(1)
  Variable<Spectrum> radiance();

  /**
   * The angle that the bottom edge of the spherical cap makes with the positive z-axis in the light's object space,
   * in degrees.
   */
  @HanaProperty(2)
  Variable<Double> thetaA();

  /**
   * The transformation from the light's object space to world space.
   */
  @HanaProperty(3)
  Variable<Transform> toWorld();

  @HanaProperty(4)
  Variable<Double> samplingWeight();
}