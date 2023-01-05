package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.mikumikubake.opengl.renderer00.Renderer00;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MmdSurfaceRenderer00 {
  void render(Renderer00 renderer00);

  void render(Renderer00 renderer00, Supplier<Stream<Integer>> materialIndexStreamSupplier);
}
