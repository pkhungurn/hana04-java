package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;

public class BoxFilterExtensions {
  public static class Evaluator_ implements ReconstructionFilters.Evaluator {
    private static final Evaluator_ I = new Evaluator_();
    private Evaluator_() {
      // NO-OP
    }

    @Override
    public double getRadius() {
      return 0.5;
    }

    @Override
    public double eval(double x) {
      if (x > 0.5 || x < -0.5) {
        return 0;
      } else {
        return 1;
      }
    }
  }

  public static class EvaluatorVv extends Constant<ReconstructionFilters.Evaluator>
      implements ReconstructionFilters.Evaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = BoxFilter.class,
      extensionClass = ReconstructionFilters.Evaluator.Vv.class)
    EvaluatorVv(BoxFilter filter) {
      super(Evaluator_.I);
    }
  }
}
