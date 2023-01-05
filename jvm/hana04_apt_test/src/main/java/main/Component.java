package main;

import hana04.base.extension.HanaUberFactory;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {
    hana04.base.Module.class,
    hana04.serialize.Module.class,
    hana04.gfxbase.serialize.Module.class,
    Module.class,
})
public interface Component extends hana04.base.Component {
  HanaUberFactory uberFactory();
}
