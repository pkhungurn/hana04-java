package hana04.shakuyaku.bsdf.classes.twosided;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.shakuyaku.bsdf.Bsdf;

import javax.inject.Inject;

public class TwoSidedBsdfs {
  public static Wrapped<Bsdf> frontSide(TwoSidedBsdf instance) {
    return instance.side().value().get(0);
  }

  public static Wrapped<Bsdf> backSide(TwoSidedBsdf instance) {
    if (instance.side().value().size() > 1) {
      return instance.side().value().get(1);
    } else {
      return instance.side().value().get(0);
    }
  }

  @HanaDeclareBuilder(TwoSidedBsdf.class)
  public static class TwoSidedBsdfBuilder extends TwoSidedBsdf__Impl__Builder<TwoSidedBsdfBuilder> {
    @Inject
    public TwoSidedBsdfBuilder(TwoSidedBsdf__ImplFactory factory) {
      super(factory);
    }

    public static TwoSidedBsdfBuilder builder(Component component) {
      return component.uberFactory().create(TwoSidedBsdfBuilder.class);
    }
  }

  public static class UnwrappedFrontBsdf
      extends DerivedVersionedValue<Bsdf> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = UnwrappedFrontBsdf.class)
    UnwrappedFrontBsdf(TwoSidedBsdf bsdf, HanaUnwrapper unwrapper) {
      super(
          ImmutableList.of(bsdf.side()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> TwoSidedBsdfs.frontSide(bsdf).unwrap(unwrapper));
    }
  }

  public static class UnwrappedBackBsdf
      extends DerivedVersionedValue<Bsdf> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = UnwrappedBackBsdf.class)
    UnwrappedBackBsdf(TwoSidedBsdf bsdf, HanaUnwrapper unwrapper) {
      super(
          ImmutableList.of(bsdf.side()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> TwoSidedBsdfs.backSide(bsdf).unwrap(unwrapper));
    }
  }

  public static class TwoSidedBsdfValidator implements Validator {
    private final TwoSidedBsdf instance;

    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = Validator.class)
    public TwoSidedBsdfValidator(TwoSidedBsdf instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(instance.side().value().size() >= 1);
      Preconditions.checkArgument(instance.side().value().size() <= 2);
    }
  }
}
