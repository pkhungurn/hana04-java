package hana04.yuri.request.onepassrender;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.distrib.request.Request;

import java.nio.file.FileSystem;

public class OnePassRenderRequestExtensions {
  public static class RunnerVv extends DerivedVersionedValue<Request.Runner> implements Request.Runner.Vv {
    @HanaDeclareExtension(
      extensibleClass = OnePassRenderRequest.class,
      extensionClass = Request.Runner.Vv.class)
    public RunnerVv(OnePassRenderRequest request, HanaUnwrapper unwrapper, FileSystem fileSystem) {
      super(
        ImmutableList.of(
          request.film(),
          request.camera(),
          request.integrand(),
          request.scene()),
        (self, dependencies) -> self.version() + 1,
        () -> new OnePassRenderRequestRunner(request, unwrapper, fileSystem));
    }
  }
}
