package hana04.base.changeprop.util;

import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;

import java.util.Optional;
import java.util.function.Function;

public class VvOptionalTransformWithVvExtractor<T, V>
  extends AbstractDerivedSubject implements VersionedValue<Optional<V>> {
  private final VersionedValue<? extends Optional<? extends T>> input;
  private final Function<T, VersionedValue<? extends V>> extractor;
  private Optional<VersionedValue<? extends V>> outputVv = Optional.empty();
  private Optional<V> output = Optional.empty();

  public VvOptionalTransformWithVvExtractor(
    VersionedValue<? extends Optional<? extends T>> input,
    Function<T, VersionedValue<? extends V>> extractor) {
    this.input = input;
    this.extractor = extractor;
    input.addObserver(this);
    forceUpdate();
  }

  @Override
  protected long updateInternal() {
    input.update();
    outputVv.ifPresent(versionedValue -> versionedValue.removeObserver(this));
    outputVv = input.value().map(extractor::apply);
    outputVv.ifPresent(versionedValue -> versionedValue.addObserver(this));
    output = outputVv.map(VersionedValue::value);

    long newVersion = version()+1;
    newVersion = Math.max(newVersion, input.version());
    newVersion = outputVv.isPresent() ? Math.max(newVersion, outputVv.get().version()) : newVersion;
    return newVersion;
  }

  @Override
  public Optional<V> value() {
    return output;
  }
}
