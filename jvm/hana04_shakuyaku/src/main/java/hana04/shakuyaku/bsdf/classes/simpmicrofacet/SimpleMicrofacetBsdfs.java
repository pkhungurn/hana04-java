package hana04.shakuyaku.bsdf.classes.simpmicrofacet;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.UnwrapVv;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.constant.ConstantTwoDimTextures;

import javax.inject.Inject;
import javax.inject.Provider;

public class SimpleMicrofacetBsdfs {
  public static class UnwrappedDiffuseTextureVv extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = UnwrappedDiffuseTextureVv.class)
    UnwrappedDiffuseTextureVv(SimpleMicrofacetBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.diffuseReflectance(), unwrapper);
    }
  }

  public static class UnwrappedRoughnessTextureVv extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = UnwrappedRoughnessTextureVv.class)
    UnwrappedRoughnessTextureVv(SimpleMicrofacetBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.roughness(), unwrapper);
    }
  }

  @HanaDeclareBuilder(SimpleMicrofacetBsdf.class)
  public static class SimpleMicrofacetBsdfBuilder extends SimpleMicrofacetBsdf__Impl__Builder<SimpleMicrofacetBsdfBuilder> {
    @Inject
    public SimpleMicrofacetBsdfBuilder(
        SimpleMicrofacetBsdf__ImplFactory factory,
        Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantTextureBuilder) {
      super(factory);
      diffuseReflectance(constantTextureBuilder.get().spectrum(new Rgb(0.5, 0.5, 0.5)).build());
      roughness(constantTextureBuilder.get().spectrum(new Rgb(0.1, 0.1, 0.1)).build());
      intIor(1.49);
      extIor(1.000277);
    }

    public static SimpleMicrofacetBsdfBuilder builder(Component component) {
      return component.uberFactory().create(SimpleMicrofacetBsdfBuilder.class);
    }
  }
}
