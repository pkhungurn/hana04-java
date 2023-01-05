package hana04.base.extension.placeholders;

import hana04.base.extension.HanaExtensionFactory;
import hana04.base.extension.HanaExtensionFactoryMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class HanaObject__HanaExtensionFactoryMap implements HanaExtensionFactoryMap {
  private Map<Class<?>, HanaObject__HanaExtensionFactory> classToExtensionFactory;

  @Inject
  public HanaObject__HanaExtensionFactoryMap(Map<Class<?>, HanaObject__HanaExtensionFactory> classToExtensionFactory) {
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
