package hana04.base.extension;

import com.google.common.base.Preconditions;
import hana04.base.extension.annotation.HanaExtensibleSuperclass;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public final class HanaExtensionUberFactory {
  private final Map<Class<?>, Class<?>> superclassMap;
  private final Map<Class<?>, HanaExtensionFactoryMap> extensionFactoryMapMap;

  @Inject
  public HanaExtensionUberFactory(@HanaExtensibleSuperclass Map<Class<?>, Class<?>> superclassMap,
                              Map<Class<?>, HanaExtensionFactoryMap> extensionFactoryMapMap) {
    this.superclassMap = superclassMap;
    this.extensionFactoryMapMap = extensionFactoryMapMap;
  }

  public boolean supportsExtension(
    Class<? extends HanaExtensible> extensibleClass, Class<?> extensionClass) {
    Class currentExtensible = extensibleClass;
    while (true) {
      HanaExtensionFactoryMap extensionFactoryMap = extensionFactoryMapMap.get(currentExtensible);
      Preconditions.checkNotNull(extensionFactoryMap, "currentExtensible = " + currentExtensible.getCanonicalName());
      if (extensionFactoryMap.containsKey(extensionClass)) {
        return true;
      }
      if (!superclassMap.containsKey(currentExtensible)) {
        break;
      }
      currentExtensible = superclassMap.get(currentExtensible);
    }
    return false;
  }

  public <T extends HanaExtensible, V> V createExtension(
    T extensible, Class<? extends HanaExtensible> extensibleClass, Class<V> extensionClass) {
    Class currentExtensible = extensibleClass;
    while (true) {
      HanaExtensionFactoryMap extensionFactoryMap = extensionFactoryMapMap.get(currentExtensible);
      Preconditions.checkNotNull(extensionFactoryMap, "currentExtensible = " + currentExtensible.getCanonicalName());
      if (extensionFactoryMap.containsKey(extensionClass)) {
        return (V) extensionFactoryMap.get(extensionClass).createFromExtensible(extensible);
      }
      if (!superclassMap.containsKey(currentExtensible)) {
        break;
      }
      currentExtensible = superclassMap.get(currentExtensible);
    }
    throw new RuntimeException("Extension " + extensionClass.getName() + " for class " + extensibleClass.getName() +
      " is not supported.");
  }
}
