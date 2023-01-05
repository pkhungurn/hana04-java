package hana04.shakuyaku.surface.intervaled;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.extension.validator.Validator;

import javax.inject.Inject;

public class IntervaledSurfacePatchIntervals {

  @HanaDeclareBuilder(IntervaledSurfacePatchInterval.class)
  public static class IntervaledSurfacePatchIntervalBuilder
      extends IntervaledSurfacePatchInterval__Impl__Builder<IntervaledSurfacePatchIntervalBuilder> {
    @Inject
    public IntervaledSurfacePatchIntervalBuilder(IntervaledSurfacePatchInterval__ImplFactory factory) {
      super(factory);
    }

    public static IntervaledSurfacePatchIntervalBuilder builder(Component component) {
      return component.uberFactory().create(IntervaledSurfacePatchIntervalBuilder.class);
    }
  }

  public static class IntervaledSurfacePatchIntervalValidator implements Validator {
    private final IntervaledSurfacePatchInterval instance;

    @HanaDeclareExtension(
        extensibleClass = IntervaledSurfacePatchInterval.class,
        extensionClass = Validator.class)
    public IntervaledSurfacePatchIntervalValidator(IntervaledSurfacePatchInterval instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(0 <= instance.startPatchIndex());
      Preconditions.checkArgument(instance.startPatchIndex() < instance.endPatchIndex());
    }
  }
}
