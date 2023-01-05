package hana04.yuri.rfilter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.special.Erf;

import javax.inject.Inject;

public class GaussianFilters {
  @HanaDeclareBuilder(GaussianFilter.class)
  public static class GaussianFilterBuilder extends GaussianFilter__Impl__Builder<GaussianFilterBuilder> {
    @Inject
    public GaussianFilterBuilder(GaussianFilter__ImplFactory factory) {
      super(factory);
      radius(2.0);
      stddev(5.0);
    }

    public static GaussianFilterBuilder builder(Component component) {
      return component.uberFactory().create(GaussianFilterBuilder.class);
    }
  }

  public static class Evaluator_ implements ReconstructionFilters.Evaluator {
    private Gaussian gaussian;
    private final double radius;
    private final double sigma;
    private final double offset;
    private final double normalization;

    Evaluator_(double radius, double sigma) {
      this.radius = radius;
      this.sigma = sigma;
      this.gaussian = new Gaussian(0, sigma);
      this.offset = this.gaussian.value(this.radius);
      this.normalization =
          0.5 * (Erf.erf(radius / Math.sqrt(2 * sigma * sigma))
                     - Erf.erf(-radius / Math.sqrt(2 * sigma * sigma)))
              - 2 * offset;
    }

    @Override
    public double getRadius() {
      return radius;
    }

    @Override
    public double eval(double x) {
      if (x > radius || x < -radius) {
        return 0;
      } else {
        return (gaussian.value(x) - offset) / normalization;
      }
    }
  }

  public static class EvaluatorVv extends DerivedVersionedValue<ReconstructionFilters.Evaluator>
      implements ReconstructionFilters.Evaluator.Vv {
    @HanaDeclareExtension(
        extensibleClass = GaussianFilter.class,
        extensionClass = ReconstructionFilters.Evaluator.Vv.class)
    EvaluatorVv(GaussianFilter filter) {
      super(
          ImmutableList.of(filter.radius(), filter.stddev()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new Evaluator_(filter.radius().value(), filter.stddev().value()));
    }
  }

  public static class GaussianFilterValidator implements Validator {
    private final GaussianFilter instance;

    @HanaDeclareExtension(
        extensibleClass = GaussianFilter.class,
        extensionClass = Validator.class)
    public GaussianFilterValidator(GaussianFilter instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(instance.radius().value() > 0);
      Preconditions.checkArgument(instance.stddev().value() > 0);

    }
  }
}
