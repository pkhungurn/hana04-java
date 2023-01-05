package hana04.base.changeprop.util;

import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;

import java.util.Optional;
import java.util.function.Function;

public class VvOptionalWrappedToVvOptionalAdaptor<T, V> extends AbstractDerivedSubject
  implements VersionedValue<Optional<V>> {
  private final VersionedValue<Optional<Wrapped<T>>> wrapped;
  private final HanaUnwrapper unwrapper;
  private final Function<T, VersionedValue<V>> extractor;
  private Optional<VersionedValue<V>> currentVv = Optional.empty();
  private Optional<V> currentValue = Optional.empty();

  public VvOptionalWrappedToVvOptionalAdaptor(
    VersionedValue<Optional<Wrapped<T>>> wrapped, Function<T, VersionedValue<V>> extractor, HanaUnwrapper unwrapper) {
    this.wrapped = wrapped;
    this.unwrapper = unwrapper;
    this.extractor = extractor;
    wrapped.addObserver(this);
    forceUpdate();
  }

  @Override
  protected long updateInternal() {
    wrapped.update();
    long newVersion = wrapped.version();
    currentVv.ifPresent(vVersionedValue -> vVersionedValue.removeObserver(this));
    if (!wrapped.value().isPresent()) {
      currentVv = Optional.empty();
    } else {
      T source = wrapped.value().get().unwrap(unwrapper);
      VersionedValue<V> vv = extractor.apply(source);
      vv.addObserver(this);
      currentVv = Optional.of(vv);
      newVersion = Math.max(newVersion, vv.version());
    }
    currentValue = currentVv.map(VersionedValue::value);
    return Math.max(version() + 1, newVersion);
  }

  @Override
  public Optional<V> value() {
    return currentValue;
  }
}
