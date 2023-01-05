package hana04.shakuyaku.texture.twodim.constant;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

import javax.inject.Inject;

public class ConstantTwoDimTextures {
  @HanaDeclareBuilder(ConstantTwoDimTexture.class)
  public static class ConstantTwoDimTextureBuilder extends ConstantTwoDimTexture__Impl__Builder<ConstantTwoDimTextureBuilder> {
    @Inject
    public ConstantTwoDimTextureBuilder(ConstantTwoDimTexture__ImplFactory factory) {
      super(factory);
      spectrum(new Rgb(0.5, 0.5, 0.5));
      alpha(1.0);
    }

    public static ConstantTwoDimTextureBuilder builder(Component component) {
      return component.uberFactory().create(ConstantTwoDimTextureBuilder.class);
    }

    public static TextureTwoDim create(Spectrum spectrum, Component component) {
      return builder(component)
        .spectrum(spectrum)
        .build();
    }
  }
}
