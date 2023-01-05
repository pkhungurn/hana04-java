package hana04.yuri.trial.t01;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.distrib.request.Request;

import java.nio.file.FileSystem;

public class RenderFractalImageToFilmRequestExtensions {
  public static class RequestRunnerVv
    extends DerivedVersionedValue<Request.Runner> implements Request.Runner.Vv {
    @HanaDeclareExtension(
      extensibleClass = RenderFractalImageToFilmRequest.class,
      extensionClass = Request.Runner.Vv.class)
    public RequestRunnerVv(RenderFractalImageToFilmRequest request, FileSystem fileSystem) {
      super(
        ImmutableList.of(
          request.centerX(), request.centerY(),
          request.scale(),
          request.sampler(),
          request.film()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new RenderFractalImageToFilmRequestRunner(request, fileSystem));
    }
  }
}
