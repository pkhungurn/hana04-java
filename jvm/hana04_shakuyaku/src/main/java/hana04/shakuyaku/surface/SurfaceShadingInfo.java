package hana04.shakuyaku.surface;

import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.VersionedValue;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.emitter.Emitter;

import java.util.Optional;

public class SurfaceShadingInfo {
  private final HanaUnwrapper unwrapper;
  private final SurfacePatchIntervalInfo patchIntervalInfo;

  public SurfaceShadingInfo(SurfacePatchIntervalInfo patchIntervalInfo, HanaUnwrapper unwrapper) {
    this.unwrapper = unwrapper;
    this.patchIntervalInfo = patchIntervalInfo;
  }

  /**
   * The BSDf of the patch with the given index.
   */
  public Bsdf getBsdf(int patchIndex) {
    return patchIntervalInfo.mapPatchToPatchInterval(patchIndex).bsdf().unwrap(unwrapper);
  }

  /**
   * The emitter attached to the patch with the given index.
   */
  public Optional<Emitter> getEmitter(int patchIndex) {
    return patchIntervalInfo.mapPatchToPatchInterval(patchIndex).emitter().map(unwrapper::unwrap);
  }

  public interface Vv extends VersionedValue<SurfaceShadingInfo> {
    // NO-OP
  }
}
