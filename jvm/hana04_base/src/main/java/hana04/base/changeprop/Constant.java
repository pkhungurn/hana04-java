package hana04.base.changeprop;

public class Constant<T> implements VersionedValue<T> {
  private final T value;

  public Constant(T value) {
    this.value = value;
  }

  @Override
  public T value() {
    return value;
  }

  @Override
  public void addObserver(DirtinessObserver observer) {
    // NO-OP
  }

  @Override
  public void removeObserver(DirtinessObserver observer) {
    // NO-OP
  }

  @Override
  public long version() {
    return 0;
  }

  @Override
  public void update() {
    // NO-OP
  }

  @Override
  public boolean isDirty() {
    return false;
  }
}
