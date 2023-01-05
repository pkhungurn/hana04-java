package hana04.base.changeprop;

import java.util.List;
import java.util.function.Function;

public abstract class ChangePropUtil {
  public static <T, V extends VersionedSubject> VersionedValue<T> extractVersionedValue(
    V subject, Function<V, T> extractor) {
    return new VersionedValue<T>() {
      @Override
      public T value() {
        return extractor.apply(subject);
      }

      @Override
      public void addObserver(DirtinessObserver observer) {
        subject.addObserver(observer);
      }

      @Override
      public void removeObserver(DirtinessObserver observer) {
        subject.removeObserver(observer);
      }

      @Override
      public long version() {
        return subject.version();
      }

      @Override
      public void update() {
        subject.update();
      }

      @Override
      public boolean isDirty() {
        return subject.isDirty();
      }
    };
  }

  public static long largestBetweenIncSelfAndDeps(VersionedSubject self, List<VersionedSubject> dependencies) {
    long newVersion = self.version() + 1;
    if (dependencies.size() == 0) {
      return newVersion;
    } else {
      long version = newVersion;
      for (VersionedSubject dependency : dependencies) {
        version = Math.max(version, dependency.version());
      }
      return version;
    }
  }
}
