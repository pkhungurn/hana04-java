package hana04.base.changeprop;

import java.util.WeakHashMap;

public class DirtinessObserverManager {
  private WeakHashMap<DirtinessObserver, Integer> observers = new WeakHashMap<>();
  private VersionedSubject subject;

  public DirtinessObserverManager(VersionedSubject subject) {
    this.subject = subject;
  }

  public synchronized void addObserver(DirtinessObserver observer) {
    observers.put(observer, 0);
  }

  public synchronized void removeObserver(DirtinessObserver observer) {
    observers.remove(observer);
  }

  public synchronized void notifyObservers() {
    for (DirtinessObserver observer : observers.keySet()) {
      observer.notifiedDirtiness(subject);
    }
  }
}
