package hana04.base.changeprop.util;

import com.google.common.base.Preconditions;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;

import java.util.function.Function;

public class VvWrappedToVvAdaptor<T, V> extends AbstractDerivedSubject implements VersionedValue<V> {
  private final VersionedValue<Wrapped<T>> wrapped;
  private final HanaUnwrapper unwrapper;
  private final Function<T, VersionedValue<V>> extractor;
  private VersionedValue<V> versionedValue;


  public VvWrappedToVvAdaptor(VersionedValue<Wrapped<T>> wrapped,
                              Function<T, VersionedValue<V>> extractor,
                              HanaUnwrapper unwrapper) {
    Preconditions.checkNotNull(wrapped);
    Preconditions.checkNotNull(unwrapper);
    Preconditions.checkNotNull(extractor);
    this.wrapped = wrapped;
    this.unwrapper = unwrapper;
    this.extractor = extractor;
    wrapped.addObserver(this);
    dirtyBit.set(true);
    update();
  }

  @Override
  protected long updateInternal() {
    long currentVersion = version();
    wrapped.update();
    long wrappedVersion = wrapped.version();
    T unwrapped = unwrapper.unwrap(wrapped.value());
    VersionedValue<V> newVersionedValue = extractor.apply(unwrapped);
    newVersionedValue.update();
    if (newVersionedValue != versionedValue) {
      if (versionedValue != null) {
        versionedValue.removeObserver(this);
      }
      versionedValue = newVersionedValue;
      versionedValue.addObserver(this);
    }
    long providerVersion = versionedValue.version();
    return Math.max(currentVersion + 1, Math.max(wrappedVersion, providerVersion));
  }

  @Override
  public V value() {
    return versionedValue.value();
  }

  public static class Builder<T, V> {
    private VersionedValue<Wrapped<T>> wrapped;
    private final HanaUnwrapper unwrapper;
    private Function<T, VersionedValue<V>> extractor;

    public Builder(HanaUnwrapper unwrapper) {
      this.unwrapper = unwrapper;
    }

    public Builder wrapped(VersionedValue<Wrapped<T>> wrapped) {
      this.wrapped = wrapped;
      return this;
    }

    public Builder extractor(Function<T, VersionedValue<V>> extractor) {
      this.extractor = extractor;
      return this;
    }

    public VvWrappedToVvAdaptor<T, V> build() {
      return new VvWrappedToVvAdaptor<>(wrapped, extractor, unwrapper);
    }
  }
}
