package hana04.distrib.testing;

import hana04.base.Component;

import javax.inject.Singleton;

@Singleton
/*
@Component(
  modules = {
    hana04.base.Module.class,
    hana04.distrib.Module.class,
    FileResolverModule.class,
    FileSystemModule.class,
  }
)
*/
public interface TestComponent extends Component, hana04.distrib.Component {
}
