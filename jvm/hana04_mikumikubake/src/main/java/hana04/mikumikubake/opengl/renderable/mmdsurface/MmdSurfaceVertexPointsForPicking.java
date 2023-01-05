package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.base.extension.HanaExtensible;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;

@HanaDeclareExtensible(HanaExtensible.class)
public interface MmdSurfaceVertexPointsForPicking extends HanaExtensible {
  @HanaProperty(1)
  MmdSurface mmdSurface();
}
