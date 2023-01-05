package hana04.base.extension;

public interface HanaExtensionFactoryMap {
  HanaExtensionFactory get(Class<?> extensionClass);
  boolean containsKey(Class<?> extensionClass);
}
