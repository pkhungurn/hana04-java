package hana04.base.changeprop;

import java.util.function.Consumer;

public class Variable<Value> implements VersionedValue<Value> {
  private Value value;
  private final DirtinessObserverManager observerManager;
  private final VersionManager versionManager;

  public Variable(Value value) {
    this.value = value;
    this.observerManager = new DirtinessObserverManager(this);
    this.versionManager = new VersionManager();
  }

  @Override
  public Value value() {
    return value;
  }

  public void set(Value value) {
    this.value = value;
    versionManager.bumpVersion();
    observerManager.notifyObservers();
  }

  public void mutate(Consumer<Value> mutator) {
    mutator.accept(value);
    versionManager.bumpVersion();
    observerManager.notifyObservers();
  }

  @Override
  public void addObserver(DirtinessObserver observer) {
    observerManager.addObserver(observer);
  }

  @Override
  public void removeObserver(DirtinessObserver observer) {
    observerManager.removeObserver(observer);
  }

  @Override
  public long version() {
    return versionManager.getVersion();
  }

  @Override
  public void update() {
    // A variable cannot be updated.
  }

  @Override
  public boolean isDirty() {
    return false;
  }
}
