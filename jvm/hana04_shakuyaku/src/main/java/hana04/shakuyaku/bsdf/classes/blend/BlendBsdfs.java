package hana04.shakuyaku.bsdf.classes.blend;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.UnwrapVv;
import hana04.base.changeprop.util.VvTransform;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.BsdfBasicProperties;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

import javax.inject.Inject;

public class BlendBsdfs {
  public static class UnwrappedAlphaTexture extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = UnwrappedAlphaTexture.class)
    UnwrappedAlphaTexture(BlendBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.alpha(), unwrapper);
    }
  }

  public static class UnwrappedFirstBsdf extends UnwrapVv<Bsdf> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = UnwrappedFirstBsdf.class)
    UnwrappedFirstBsdf(BlendBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.first(), unwrapper);
    }
  }

  public static class UnwrappedSecondBsdf extends UnwrapVv<Bsdf> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = UnwrappedSecondBsdf.class)
    UnwrappedSecondBsdf(BlendBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.second(), unwrapper);
    }
  }

  public static class FirstBasicPropertiesVv extends VvTransform<Bsdf, BsdfBasicProperties> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = FirstBasicPropertiesVv.class)
    FirstBasicPropertiesVv(BlendBsdf bsdf) {
      super(
        bsdf.getExtension(UnwrappedFirstBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfBasicProperties.Vv.class));
    }
  }

  public static class SecondBasicPropertiesVv extends VvTransform<Bsdf, BsdfBasicProperties> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = SecondBasicPropertiesVv.class)
    SecondBasicPropertiesVv(BlendBsdf bsdf) {
      super(
        bsdf.getExtension(UnwrappedSecondBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfBasicProperties.Vv.class));
    }
  }

  public static class BasicProperties implements BsdfBasicProperties {
    private final BsdfBasicProperties firstProperties;
    private final BsdfBasicProperties secondProperties;

    BasicProperties(BsdfBasicProperties firstProperties, BsdfBasicProperties secondProperties) {
      this.firstProperties = firstProperties;
      this.secondProperties = secondProperties;
    }

    @Override
    public boolean containsPassThrough() {
      return firstProperties.containsPassThrough() || secondProperties.containsPassThrough();
    }

    @Override
    public boolean allowsTransmission() {
      return firstProperties.allowsTransmission() || secondProperties.allowsTransmission();
    }
  }

  public static class BasicPropertiesVv
    extends DerivedVersionedValue<BsdfBasicProperties>
    implements BsdfBasicProperties.Vv {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = BsdfBasicProperties.Vv.class)
    BasicPropertiesVv(BlendBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(FirstBasicPropertiesVv.class),
          bsdf.getExtension(SecondBasicPropertiesVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new BasicProperties(
          bsdf.getExtension(FirstBasicPropertiesVv.class).value(),
          bsdf.getExtension(SecondBasicPropertiesVv.class).value()));
    }
  }

  @HanaDeclareBuilder(BlendBsdf.class)
  public static class BlendBsdfBuilder extends BlendBsdf__Impl__Builder<BlendBsdfBuilder> {
    @Inject
    public BlendBsdfBuilder(BlendBsdf__ImplFactory factory) {
      super(factory);
    }

    public static BlendBsdfBuilder builder(Component component) {
      return component.uberFactory().create(BlendBsdfBuilder.class);
    }
  }
}
