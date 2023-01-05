package hana04.shakuyaku.bsdf.classes.diffuse;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.UnwrapVv;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.constant.ConstantTwoDimTextures;

import javax.inject.Inject;
import javax.inject.Provider;

public class DiffuseBsdfs {

  public static class UnwrappedReflectanceTextureVv extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = DiffuseBsdf.class,
      extensionClass = UnwrappedReflectanceTextureVv.class)
    UnwrappedReflectanceTextureVv(DiffuseBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.reflectance(), unwrapper);
    }
  }

  @HanaDeclareBuilder(DiffuseBsdf.class)
  public static class DiffuseBsdfBuilder extends DiffuseBsdf__Impl__Builder<DiffuseBsdfBuilder> {
    @Inject
    public DiffuseBsdfBuilder(DiffuseBsdf__ImplFactory factory,
        Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantTextureBuilder) {
      super(factory);
      reflectance(constantTextureBuilder.get().spectrum(new Rgb(1, 1, 1)).alpha(1.0).build());
    }

    public static DiffuseBsdfBuilder builder(Component component) {
      return component.uberFactory().create(DiffuseBsdfBuilder.class);
    }

    public static DiffuseBsdf create(Spectrum spectrum, Component component) {
      return builder(component).reflectance(ConstantTwoDimTextures.ConstantTwoDimTextureBuilder
          .builder(component).spectrum(spectrum).build()).build();
    }
  }
}
