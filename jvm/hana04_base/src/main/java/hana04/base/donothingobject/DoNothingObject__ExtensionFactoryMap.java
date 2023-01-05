package hana04.base.donothingobject;

import hana04.base.extension.HanaExtensionFactory;
import hana04.base.extension.HanaExtensionFactoryMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class DoNothingObject__ExtensionFactoryMap implements HanaExtensionFactoryMap {
  private final Map<Class<?>, DoNothingObject__ExtensionFactory> classToExtensionFactoryMap;

  @Inject
  public DoNothingObject__ExtensionFactoryMap(Map<Class<?>, DoNothingObject__ExtensionFactory> classToExtensionFactoryMap) {
    this.classToExtensionFactoryMap = classToExtensionFactoryMap;

  }

  @Override
  public HanaExtensionFactory get(Class<?> extensionClass) {
    return classToExtensionFactoryMap.get(extensionClass);
  }

  @Override
  public boolean containsKey(Class<?> extensionClass) {
    return classToExtensionFactoryMap.containsKey(extensionClass);
  }
}
