package hana04.base.changeprop;

/**
 * A subject (as the one in the observer pattern) that can be updated and is versioned.
 */
public interface VersionedSubject extends Versioned {
  void addObserver(DirtinessObserver observer);
  void removeObserver(DirtinessObserver observer);
}
