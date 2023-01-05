package hana04.base.donothingobject;

import hana04.base.extension.FluentBuilder;
import hana04.base.extension.FluentBuilderFactory;
import hana04.base.extension.HanaExtensionUberFactory;

import javax.inject.Inject;

public class DoNothingObjectBuilder implements FluentBuilder<DoNothingObject> {
  private final HanaExtensionUberFactory extensionUberFactory;

  DoNothingObjectBuilder(HanaExtensionUberFactory extensionUberFactory) {
    this.extensionUberFactory = extensionUberFactory;
  }

  @Override
  public DoNothingObject build() {
    return new DoNothingObject(extensionUberFactory);
  }

  public static class BuilderFactory implements FluentBuilderFactory<DoNothingObject, DoNothingObjectBuilder> {
    private final HanaExtensionUberFactory extensionUberFactory;

    @Inject
    BuilderFactory(HanaExtensionUberFactory extensionUberFactory) {
      this.extensionUberFactory = extensionUberFactory;
    }

    @Override
    public DoNothingObjectBuilder create() {
      return new DoNothingObjectBuilder(extensionUberFactory);
    }
  }
}
