package hana04.base.changeprop;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A versioned subject that keeps its "dirty" bit that tells whether the subject needs to be updated. This is the
 * default compile that should be used in most cases. The update is lazy.
 */
public abstract class AbstractVersionedSubject implements VersionedSubject {
  protected VersionManager versionManager = new VersionManager();
  protected DirtinessObserverManager dirtinessObserverManager = new DirtinessObserverManager(this);
  protected AtomicBoolean dirtyBit = new AtomicBoolean(false);

  /**
   * Update the internal state of the versioned subject.
   *
   * @return the new version number of the subject
   */
  protected abstract long updateInternal();

  /**
   * It is hard to make this atomic. In normal case, only call update on the UI thread.
   * In a multi-thread renderer, call this function judiciously.
   */
  @Override
  public void update() {
    if (!isDirty()) {
      return;
    }
    long newVersion = updateInternal();
    dirtyBit.set(false);
    versionManager.setVersion(newVersion);
  }

  protected void forceUpdate() {
    dirtyBit.set(true);
    update();
  }

  @Override
  public long version() {
    return versionManager.getVersion();
  }

  public boolean isDirty() {
    return dirtyBit.get();
  }

  protected void markDirty() {
    if (!isDirty()) {
      dirtyBit.set(true);
      dirtinessObserverManager.notifyObservers();
    }
  }

  @Override
  public void addObserver(DirtinessObserver observer) {
    dirtinessObserverManager.addObserver(observer);
  }

  @Override
  public void removeObserver(DirtinessObserver observer) {
    dirtinessObserverManager.removeObserver(observer);
  }
}
