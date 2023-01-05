package hana04.base.changeprop;

/**
 * A piece of data that can be updated and is versioned.
 */
public interface Versioned {
  long version();

  void update();

  boolean isDirty();
}
