package hana04.base.extension.placeholders;

import hana04.base.extension.HanaExtensionFactory;
import hana04.base.extension.HanaExtensionFactoryMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class HanaLateDeserializable__HanaExtensionFactoryMap implements HanaExtensionFactoryMap {
  private final Map<Class<?>, HanaLateDeserializable__HanaExtensionFactory> classToExtensionFactory;

  @Inject
  public HanaLateDeserializable__HanaExtensionFactoryMap(
      Map<Class<?>, HanaLateDeserializable__HanaExtensionFactory> classToExtensionFactory) {
    this.classToExtensionFactory = classToExtensionFactory;
  }

  @Override
  public HanaExtensionFactory get(Class<?> extensionClass) {
    return classToExtensionFactory.get(extensionClass);
  }

  @Override
  public boolean containsKey(Class<?> extensionClass) {
    return classToExtensionFactory.containsKey(extensionClass);
  }
}
