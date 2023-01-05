package hana04.mikumikubake;

import hana04.mikumikubake.surface.DirectPmdBaseGeometry;
import hana04.mikumikubake.surface.DirectPmxBaseGeometry;

/**
 * This project's type IDs start with 70000.
 */
public final class TypeIds {
  /**
   * {@link DirectPmdBaseGeometry}
   */
  public static final int TYPE_ID_DIRECT_PMD_BASE_GEOMETRY = 70001;

  /**
   * {@link DirectPmxBaseGeometry}
   */
  public static final int TYPE_ID_DIRECT_PMX_BASE_GEOMETRY = 70002;

  private TypeIds() {
    // NO-OP
  }
}
