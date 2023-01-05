package hana04.shakuyaku.surface.intervaled;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.extension.validator.Validator;

import javax.inject.Inject;

public class IntervaledSurfacePatchIntervalSpecs {
  @HanaDeclareBuilder(IntervaledSurfacePatchIntervalSpec.class)
  public static class IntervaledSurfacePatchIntervalSpecBuilder
      extends IntervaledSurfacePatchIntervalSpec__Impl__Builder<IntervaledSurfacePatchIntervalSpecBuilder> {
    @Inject
    public IntervaledSurfacePatchIntervalSpecBuilder(IntervaledSurfacePatchIntervalSpec__ImplFactory factory) {
      super(factory);
      startPatchIndex(0);
    }

    public static IntervaledSurfacePatchIntervalSpecBuilder builder(Component component) {
      return component.uberFactory().create(IntervaledSurfacePatchIntervalSpecBuilder.class);
    }
  }

  public static class IntervaledSurfacePatchIntervalSpecValidator implements Validator {
    private final IntervaledSurfacePatchIntervalSpec instance;

    @HanaDeclareExtension(
        extensibleClass = IntervaledSurfacePatchIntervalSpec.class,
        extensionClass = Validator.class)
    public IntervaledSurfacePatchIntervalSpecValidator(IntervaledSurfacePatchIntervalSpec instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(instance.startPatchIndex() >= 0);
    }
  }
}
