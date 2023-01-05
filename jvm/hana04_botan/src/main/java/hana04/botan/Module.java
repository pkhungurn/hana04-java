package hana04.botan;

import hana04.apt.annotation.HanaModule;

@HanaModule
@dagger.Module(
    includes = {
        Module__GeneratedHanaModule.class,
        GlWrapperModule.class,
    }
)
public abstract class Module {
    // NO-OP
}
