package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;

public class TentFilterExtensions {
  public static class Evaluator_ implements ReconstructionFilters.Evaluator {
    private static final Evaluator_ I = new Evaluator_();

    private Evaluator_() {
      // NO-OP
    }

    @Override
    public double getRadius() {
      return 1.0;
    }

    @Override
    public double eval(double x) {
      return Math.max(0, 1.0 - Math.abs(x));
    }
  }

  public static class EvaluatorVv extends Constant<ReconstructionFilters.Evaluator>
      implements ReconstructionFilters.Evaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = TentFilter.class,
      extensionClass = ReconstructionFilters.Evaluator.Vv.class)
    EvaluatorVv(TentFilter filter) {
      super(Evaluator_.I);
    }
  }
}
