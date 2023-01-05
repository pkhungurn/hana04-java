package hana04.base.changeprop.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedSubject;
import hana04.base.changeprop.VersionedValue;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DerivedVersionedValue<T> extends AbstractDerivedSubject implements VersionedValue<T> {
  private final List<VersionedSubject> dependencies;
  private final BiFunction<VersionedSubject, List<VersionedSubject>, Long> newVersionFunc;
  private final Supplier<T> valueFunc;
  private T value;

  public DerivedVersionedValue(List<VersionedSubject> dependencies,
                               BiFunction<VersionedSubject, List<VersionedSubject>, Long> newVersionFunc,
                               Supplier<T> valueFunc) {
    Preconditions.checkNotNull(dependencies);
    Preconditions.checkNotNull(newVersionFunc);
    Preconditions.checkNotNull(valueFunc);
    this.newVersionFunc = newVersionFunc;
    this.valueFunc = valueFunc;
    this.dependencies = dependencies;
    for (VersionedSubject subject : dependencies) {
      subject.addObserver(this);
    }
    forceUpdate();
  }

  @Override
  protected long updateInternal() {
    for (VersionedSubject subject : dependencies) {
      subject.update();
    }
    long newVersion = newVersionFunc.apply(this, dependencies);
    value = valueFunc.get();
    return newVersion;
  }

  @Override
  public T value() {
    return value;
  }

  public static class Builder<T> {
    private ImmutableList.Builder<VersionedSubject> dependencies = ImmutableList.builder();
    private BiFunction<VersionedSubject, List<VersionedSubject>, Long> newVersionFunc;
    private Supplier<T> valueFunc;

    public Builder() {
      // NO-OP
    }

    public Builder<T> addDependency(VersionedValue dependency) {
      dependencies.add(dependency);
      return this;
    }

    public Builder<T> newVersionFunc(BiFunction<VersionedSubject, List<VersionedSubject>, Long> newVersionFunc) {
      this.newVersionFunc = newVersionFunc;
      return this;
    }

    public Builder<T> valueFunc(Supplier<T> valueFunc) {
      this.valueFunc = valueFunc;
      return this;
    }

    public DerivedVersionedValue<T> build() {
      return new DerivedVersionedValue<>(dependencies.build(), newVersionFunc, valueFunc);
    }
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }
}
