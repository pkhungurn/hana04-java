package hana04.base.extension;

public interface FluentBuilderFactory<T, V extends FluentBuilder<T>> {
  V create();
}
