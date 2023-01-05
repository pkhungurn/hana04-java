package hana04.base.changeprop;

public interface VersionedValue<Value> extends VersionedSubject {
  // Return the current (possibly un-updated value). This method should not be synchronized.
  Value value();

  default Value updatedValue() {
    update();
    return value();
  }
}
