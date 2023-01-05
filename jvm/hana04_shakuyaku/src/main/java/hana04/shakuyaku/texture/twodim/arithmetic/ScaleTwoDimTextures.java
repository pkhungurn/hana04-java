package hana04.shakuyaku.texture.twodim.arithmetic;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.UnwrapVv;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

import javax.inject.Inject;

public class ScaleTwoDimTextures {
  public static class UnwrappedBaseTexture extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = UnwrappedBaseTexture.class)
    UnwrappedBaseTexture(ScaleTwoDimTexture node, HanaUnwrapper unwrapper) {
      super(
        node.texture(),
        unwrapper);
    }
  }

  @HanaDeclareBuilder(ScaleTwoDimTexture.class)
  public static class ScaleTwoDimTextureBuilder extends ScaleTwoDimTexture__Impl__Builder<ScaleTwoDimTextureBuilder> {
    @Inject
    public ScaleTwoDimTextureBuilder(ScaleTwoDimTexture__ImplFactory factory) {
      super(factory);
      alphaScale(1.0);
      spectrumScale(new Rgb(1, 1, 1));
    }

    public static ScaleTwoDimTextureBuilder builder(Component component) {
      return component.uberFactory().create(ScaleTwoDimTextureBuilder.class);
    }
  }
}
