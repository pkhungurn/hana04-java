package hana04.base.changeprop.util;

import com.google.common.base.Preconditions;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;

import java.util.function.Function;

public class VvTransform<T, V> extends AbstractDerivedSubject implements VersionedValue<V> {
  private final VersionedValue<T> input;
  private final Function<T, VersionedValue<V>> transformer;
  private VersionedValue<V> output;

  public VvTransform(VersionedValue<T> input,
                     Function<T, VersionedValue<V>> transformer) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(transformer);
    this.input = input;
    this.transformer = transformer;
    input.addObserver(this);
    forceUpdate();
  }

  @Override
  protected long updateInternal() {
    input.update();
    long inputVersion = input.version();
    VersionedValue<V> newOutput = transformer.apply(input.value());
    newOutput.update();
    if (output != null) {
      output.removeObserver(this);
    }
    output = newOutput;
    output.addObserver(this);
    long outputVersion = output.version();
    return Math.max(version() + 1, Math.max(inputVersion, outputVersion));
  }

  @Override
  public V value() {
    return output.value();
  }
}
