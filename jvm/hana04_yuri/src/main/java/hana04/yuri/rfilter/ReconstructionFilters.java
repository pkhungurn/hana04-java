package hana04.yuri.rfilter;

import hana04.base.changeprop.VersionedValue;

public class ReconstructionFilters {
  /**
   * Evaluates of the {@link ReconstructionFilter} along its axis of symmetry.
   */
  public interface Evaluator {
    /**
     * Returns the radius of the filter.
     */
    double getRadius();

    /**
     * Evaluates the filter at point {@code x}.
     */
    double eval(double x);

    interface Vv extends VersionedValue<Evaluator> {
      // NO-OP
    }
  }
}
