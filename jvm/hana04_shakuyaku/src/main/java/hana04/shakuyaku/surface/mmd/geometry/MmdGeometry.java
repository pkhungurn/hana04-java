package hana04.shakuyaku.surface.mmd.geometry;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.OptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.OptionalPmxModelVv;

/**
 * Extensions supports:
 * <ol>
 * <li>{@link ModelFilePathVv}</li>
 * <li>{@link OptionalPmdModelVv}</li>
 * <li>{@link OptionalPmxModelVv}</li>
 * <li>{@link SbtmBaseMesh.Vv}</li>
 * <li>{@link TriangleMeshInfo.Vv}</li>
 * </ol>
 */
@HanaDeclareExtensibleInterface(SurfaceGeometry.class)
public interface MmdGeometry extends SurfaceGeometry {
  // NO-OP
}
