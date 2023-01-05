package hana04.shakuyaku.bsdf.classes.alpha;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

public class AlphaBsdfs {
  @HanaDeclareBuilder(AlphaBsdf.class)
  public static class AlphaBsdfBuilder extends AlphaBsdf__Impl__Builder<AlphaBsdfBuilder> {
    @Inject
    public AlphaBsdfBuilder(AlphaBsdf__ImplFactory factory) {
      super(factory);
      baseAlpha(1.0);
    }

    public static AlphaBsdfBuilder builder(Component component) {
      return component.uberFactory().create(AlphaBsdfBuilder.class);
    }
  }
}
