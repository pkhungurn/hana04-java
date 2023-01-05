package hana04.shakuyaku.emitter.area;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;

import javax.inject.Inject;

public class AreaEmitters {
  @HanaDeclareBuilder(AreaEmitter.class)
  public static class AreaEmitterBuilder extends AreaEmitter__Impl__Builder<AreaEmitterBuilder> {
    @Inject
    public AreaEmitterBuilder(AreaEmitter__ImplFactory factory) {
      super(factory);
      samplingWeight(1.0);
    }

    public static AreaEmitterBuilder builder(Component component) {
      return component.uberFactory().create(AreaEmitterBuilder.class);
    }
  }

  public static class AreaEmitterInfo implements Emitters.EmitterInfo {
    @HanaDeclareExtension(
        extensibleClass = AreaEmitter.class,
        extensionClass = Emitters.EmitterInfo.class)
    public AreaEmitterInfo(AreaEmitter instance) {
      // NO-OP
    }

    @Override
    public EmitterType getType() {
      return EmitterType.Surface;
    }
  }
}
