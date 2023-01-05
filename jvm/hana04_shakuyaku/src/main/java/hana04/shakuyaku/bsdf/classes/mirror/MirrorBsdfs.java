package hana04.shakuyaku.bsdf.classes.mirror;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

public class MirrorBsdfs {

  @HanaDeclareBuilder(MirrorBsdf.class)
  public static class MirrorBsdfBuilder extends MirrorBsdf__Impl__Builder<MirrorBsdfBuilder> {
    @Inject
    public MirrorBsdfBuilder(MirrorBsdf__ImplFactory factory) {
      super(factory);
    }

    public static MirrorBsdfBuilder builder(Component component) {
      return component.uberFactory().create(MirrorBsdfBuilder.class);
    }
  }
}
