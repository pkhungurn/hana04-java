package hana04.shakuyaku.surface;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;
import hana04.gfxbase.gfxtype.Transform;

/**
 * If you want a subtype to work with a path tracer, you need to support the following extensions:
 * <ol>
 *   <li>{@link SurfacePatchInfo.Vv}</li>
 *   <li>{@link SurfaceShadingInfo.Vv}</li>
 * </ol>
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface Surface extends HanaObject {
  /**
   * The affine transformation from the underlying geometry from object space to world space.
   */
  Variable<Transform> toWorld();
}
