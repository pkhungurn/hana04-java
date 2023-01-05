package hana04.yuri.integrand.pathtracer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.VersionedSubject;
import hana04.base.extension.validator.Validator;

import java.util.List;

public class PathTracers {
  private PathTracers() {
    // NO-OP
  }

  public static List<VersionedSubject> allParameters(PathTracer instance) {
    return ImmutableList.of(
        instance.terminationProb(),
        instance.minDepth(),
        instance.maxDepth(),
        instance.computeAmbientIllumination());
  }

  public static class PathTracerValidator implements Validator {
    private final PathTracer instance;

    @HanaDeclareExtension(
        extensibleClass = PathTracer.class,
        extensionClass = Validator.class)
    public PathTracerValidator(PathTracer instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkState(instance.terminationProb().value() >= 0);
      Preconditions.checkState(instance.terminationProb().value() <= 1);
      if (instance.minDepth().value().isPresent()) {
        Preconditions.checkState(instance.minDepth().value().get() >= 0);
      }
      if (instance.maxDepth().value().isPresent()) {
        Preconditions.checkState(instance.maxDepth().value().get() >= 0);
      }
      if (instance.minDepth().value().isPresent() && instance.maxDepth().value().isPresent()) {
        Preconditions.checkState(instance.minDepth().value().get() <= instance.maxDepth().value().get());
      }
    }
  }
}
