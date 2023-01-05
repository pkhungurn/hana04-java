package hana04.mikumikubake.opengl.renderer00.flow;

import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;

public interface FlowMapRenderer {
  void render(Renderer00 renderer00,
              MmdSurface startSurface,
              GlTextureRect positionMap,
              double positionEpsilon,
              int outputWidth,
              int outpuHeight);
}
