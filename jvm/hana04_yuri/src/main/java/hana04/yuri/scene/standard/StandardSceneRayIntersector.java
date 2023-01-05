package hana04.yuri.scene.standard;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.gfxbase.gfxtype.Ray;
import hana04.shakuyaku.scene.standard.StandardScene;
import hana04.yuri.accel.PatchBvh;
import hana04.yuri.scene.SceneRayIntersector;
import hana04.yuri.surface.Intersection;
import hana04.shakuyaku.surface.Surface;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;


public class StandardSceneRayIntersector implements SceneRayIntersector {
  private PatchBvh bvh;

  @HanaDeclareExtension(
    extensibleClass = StandardScene.class,
    extensionClass = SceneRayIntersector.class)
  public StandardSceneRayIntersector(StandardScene standardScene, HanaUnwrapper unwrapper) {
    PatchBvh.Builder builder = new PatchBvh.Builder();
    for (Wrapped<Surface> wrapped : standardScene.surface().value()) {
      Surface surface = wrapped.unwrap(unwrapper);
      builder.addSurface(surface);
    }
    bvh = builder.build();
  }

  @Override
  public Optional<Pair<Surface, Intersection>> rayIntersect(Ray ray) {
    return bvh.rayIntersect(ray);
  }

  @Override
  public boolean checkRayIntersect(Ray ray) {
    return bvh.checkRayIntersection(ray);
  }
}
