package hana04.yuri.scene;

import hana04.gfxbase.gfxtype.Ray;
import hana04.yuri.surface.Intersection;
import hana04.shakuyaku.surface.Surface;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

/**
 * Encapsulates a scene's ray intersection acceleration structure.
 */
public interface SceneRayIntersector {
  /**
   * Intersect a ray against all surfaces stored in the scene and return the detailed intersection information.
   */
  Optional<Pair<Surface, Intersection>> rayIntersect(Ray ray);

  /**
   * Check whether the given ray intersects any surface in the scene.
   */
  boolean checkRayIntersect(Ray ray);
}
