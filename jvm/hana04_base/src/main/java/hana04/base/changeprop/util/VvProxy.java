package hana04.base.changeprop.util;

import hana04.base.changeprop.DirtinessObserver;
import hana04.base.changeprop.VersionedValue;

public class VvProxy<T> implements VersionedValue<T> {
  private final VersionedValue<? extends T> source;

  public VvProxy(VersionedValue<? extends T> source) {
    this.source = source;
  }

  @Override
  public T value() {
    return source.value();
  }

  @Override
  public void addObserver(DirtinessObserver observer) {
    source.addObserver(observer);
  }

  @Override
  public void removeObserver(DirtinessObserver observer) {
    source.removeObserver(observer);
  }

  @Override
  public long version() {
    return source.version();
  }

  @Override
  public void update() {
    source.update();
  }

  @Override
  public boolean isDirty() {
    return source.isDirty();
  }
}
