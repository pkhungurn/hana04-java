package hana04.base.changeprop.util;

import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.Versioned;
import hana04.base.changeprop.VersionedValue;

import java.util.List;
import java.util.function.Function;

public class VvListVvReduce<T, V> extends AbstractDerivedSubject implements VersionedValue<V> {
  private final VersionedValue<? extends List<? extends VersionedValue<? extends T>>> inputsVv;
  private final Function<List<? extends VersionedValue<? extends T>>, V> reducer;
  private List<? extends VersionedValue<? extends T>> inputs;
  private V output;

  public VvListVvReduce(
    VersionedValue<? extends List<? extends VersionedValue<? extends T>>> inputsVv,
    Function<List<? extends VersionedValue<? extends T>>, V> reducer) {
    this.inputsVv = inputsVv;
    this.reducer = reducer;
    inputsVv.addObserver(this);
    forceUpdate();
  }

  @Override
  protected long updateInternal() {
    inputsVv.update();
    if (inputs == null || inputsVv.value() != inputs) {
      if (inputs != null) {
        inputs.forEach(input -> input.removeObserver(this));
      }
      inputs = inputsVv.value();
      inputs.forEach(input -> input.addObserver(this));
    }
    inputs.forEach(Versioned::update);
    output = reducer.apply(inputs);

    long newVersion = Math.max(version() + 1, inputsVv.version());
    for (VersionedValue value : inputs) {
      newVersion = Math.max(newVersion, value.version());
    }
    return newVersion;
  }

  @Override
  public V value() {
    return output;
  }
}
