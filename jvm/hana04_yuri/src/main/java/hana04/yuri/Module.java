package hana04.yuri;

import hana04.apt.annotation.HanaModule;
import hana04.yuri.request.onepassrender.OnePassRenderRequestModule;
import hana04.yuri.trial.t00.FractalImageRequests;
import hana04.yuri.trial.t01.RenderFractalImageToFilmRequestModule;

@HanaModule
@dagger.Module(
    includes = {
        Module__GeneratedHanaModule.class,
        OnePassRenderRequestModule.class,
        FractalImageRequests.Module.class,
        RenderFractalImageToFilmRequestModule.class,

    }
)
public abstract class Module {
  // NO-OP
}
