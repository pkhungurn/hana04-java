package hana04.shakuyaku.surface.mmd.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.shakuyaku.surface.Surface;

import javax.inject.Inject;

public class MaterialAmbientUsageDatas {
  public static final String AMBIENT_OPT_IN = "opt-in";
  public static final String AMBIENT_OPT_OUT = "opt-out";

  public static class Vv extends DerivedVersionedValue<MaterialAmbientUsage> implements MaterialAmbientUsage.Vv {
    @HanaDeclareExtension(
        extensibleClass = Surface.class,
        extensionClass = MaterialAmbientUsage.Vv.class)
    public Vv(Surface surface) {
      super(
          ImmutableList.of(
              surface.getExtension(MaterialAmbientUsageData.class).ambientMode(),
              surface.getExtension(MaterialAmbientUsageData.class).materialWithAmbientOpting()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> surface.getExtension(MaterialAmbientUsageData.class).getExtension(MaterialAmbientUsage.class));
    }
  }

  @HanaDeclareBuilder(MaterialAmbientUsageData.class)
  public static class MaterialAmbientUsageDataBuilder
      extends MaterialAmbientUsageData__Impl__Builder<MaterialAmbientUsageDataBuilder> {
    @Inject
    public MaterialAmbientUsageDataBuilder(MaterialAmbientUsageData__ImplFactory factory) {
      super(factory);
      ambientMode(AMBIENT_OPT_IN);
    }

    public static MaterialAmbientUsageDataBuilder builder(Component component) {
      return component.uberFactory().create(MaterialAmbientUsageDataBuilder.class);
    }

    @Override
    public MaterialAmbientUsageDataBuilder ambientMode(String ambientMode) {
      Preconditions.checkArgument(ambientMode.equals(AMBIENT_OPT_IN) || ambientMode.equals(
          AMBIENT_OPT_OUT));
      super.ambientMode(ambientMode);
      return this;
    }
  }

  public static class MaterialAmbientUsageDataValidator implements Validator {
    private final MaterialAmbientUsageData instance;

    @HanaDeclareExtension(
        extensibleClass = MaterialAmbientUsageData.class,
        extensionClass = Validator.class)
    public MaterialAmbientUsageDataValidator(MaterialAmbientUsageData instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkState(
          instance.ambientMode().value().equals(MaterialAmbientUsageDatas.AMBIENT_OPT_IN) ||
              instance.ambientMode().value().equals(MaterialAmbientUsageDatas.AMBIENT_OPT_OUT));
    }
  }

  public static class MaterialAmbientUsage_ implements MaterialAmbientUsage {
    private final MaterialAmbientUsageData instance;

    @HanaDeclareExtension(
        extensibleClass = MaterialAmbientUsageData.class,
        extensionClass = MaterialAmbientUsage.class)
    public MaterialAmbientUsage_(MaterialAmbientUsageData instance) {
      this.instance = instance;
    }

    @Override
    public boolean shouldMaterialUseAmbient(String name) {
      boolean hasOpting = instance.materialWithAmbientOpting().value().contains(name);
      if (instance.ambientMode().value().equals(MaterialAmbientUsageDatas.AMBIENT_OPT_IN)) {
        return hasOpting;
      } else {
        return !hasOpting;
      }
    }
  }
}
