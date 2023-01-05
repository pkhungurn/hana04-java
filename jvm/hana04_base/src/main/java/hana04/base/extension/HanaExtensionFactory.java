package hana04.base.extension;

public interface HanaExtensionFactory<T extends HanaExtensible> {
  Object create(T extensible);

  default Object createFromExtensible(HanaExtensible extensible) {
    return create((T) extensible);
  }
}
