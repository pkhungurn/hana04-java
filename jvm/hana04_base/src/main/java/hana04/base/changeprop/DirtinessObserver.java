package hana04.base.changeprop;

public interface DirtinessObserver {
  void notifiedDirtiness(VersionedSubject subject);
}
