package hana04.base.changeprop;

public abstract class AbstractDerivedSubject extends AbstractVersionedSubject implements DirtinessObserver {
  @Override
  public void notifiedDirtiness(VersionedSubject subject) {
    boolean wasDirty = dirtyBit.getAndSet(true);
    if (!wasDirty) {
      dirtinessObserverManager.notifyObservers();
    }
  }
}
