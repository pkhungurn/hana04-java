package hana04.shakuyaku.bsdf.classes.pmx;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.UnwrapVv;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs;
import hana04.shakuyaku.bsdf.classes.diffuse.DiffuseBsdfs;
import hana04.shakuyaku.bsdf.classes.passthrough.PassThroughBsdfs;
import hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdfs;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTexture;
import hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTextures;

import javax.inject.Inject;
import javax.inject.Provider;

public class PmxBsdfs {
  public static class UnwrappedTextureVv
    extends UnwrapVv<TextureTwoDim> {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = UnwrappedTextureVv.class)
    UnwrappedTextureVv(PmxBsdf bsdf, HanaUnwrapper unwrapper) {
      super(bsdf.texture(), unwrapper);
    }
  }

  public static class ScaleTextureVv extends AbstractDerivedSubject implements VersionedValue<TextureTwoDim> {
    private final PmxBsdf node;
    private final ScaleTwoDimTexture texture;

    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = ScaleTextureVv.class)
    ScaleTextureVv(PmxBsdf node,
                   Provider<ScaleTwoDimTextures.ScaleTwoDimTextureBuilder> scaleTextureBuilder) {
      this.node = node;
      node.getExtension(UnwrappedTextureVv.class).addObserver(this);
      node.alpha().addObserver(this);
      node.diffuseReflectance().addObserver(this);
      this.texture = scaleTextureBuilder.get()
        .texture(node.getExtension(UnwrappedTextureVv.class).value())
        .alphaScale(node.alpha().value())
        .spectrumScale(node.diffuseReflectance().value())
        .build();
      forceUpdate();
    }

    @Override
    protected long updateInternal() {
      node.getExtension(UnwrappedTextureVv.class).update();
      node.alpha().update();
      node.diffuseReflectance().update();

      texture.texture().set(node.getExtension(UnwrappedTextureVv.class).value().wrapped());
      texture.alphaScale().set(node.alpha().value());
      texture.spectrumScale().set(node.diffuseReflectance().value());

      long newVersion = version() + 1;
      newVersion = Math.max(newVersion, node.getExtension(UnwrappedTextureVv.class).version());
      newVersion = Math.max(newVersion, node.alpha().version());
      newVersion = Math.max(newVersion, node.diffuseReflectance().version());
      return newVersion;
    }

    @Override
    public TextureTwoDim value() {
      return texture;
    }
  }

  public static class InternalBsdfVv extends AbstractDerivedSubject implements VersionedValue<Bsdf> {
    private final PmxBsdf node;
    private final Bsdf internalBsdf;

    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = InternalBsdfVv.class)
    InternalBsdfVv(PmxBsdf node,
                   Provider<BlendBsdfs.BlendBsdfBuilder> blendBsdfBuilder,
                   Provider<TwoSidedBsdfs.TwoSidedBsdfBuilder> twoSidedBsdfBuilder,
                   Provider<DiffuseBsdfs.DiffuseBsdfBuilder> diffuseBsdfBuilder,
                   Provider<PassThroughBsdfs.PassThroughBsdfBuilder> passthroughBsdfBuilder) {
      this.node = node;
      node.getExtension(ScaleTextureVv.class).addObserver(this);
      TextureTwoDim texture = node.getExtension(ScaleTextureVv.class).value();
      internalBsdf = blendBsdfBuilder.get()
        .alpha(texture)
        .first(twoSidedBsdfBuilder.get()
          .addSide(diffuseBsdfBuilder.get().reflectance(texture).build())
          .build())
        .second(passthroughBsdfBuilder.get().build())
        .build();
    }

    @Override
    protected long updateInternal() {
      node.getExtension(ScaleTextureVv.class).update();
      return Math.max(version() + 1, node.getExtension(ScaleTextureVv.class).version());
    }

    @Override
    public Bsdf value() {
      return internalBsdf;
    }
  }

  @HanaDeclareBuilder(PmxBsdf.class)
  public static class PmxBsdfBuilder extends PmxBsdf__Impl__Builder<PmxBsdfBuilder> {
    @Inject
    public PmxBsdfBuilder(PmxBsdf__ImplFactory factory) {
      super(factory);
      alpha(1.0);
      displayBothSides(false);
      isOpaque(false);
      edgeColor(new Rgb(0, 0, 0));
      edgeThickness(1.0);
      drawEdge(false);
    }

    public static PmxBsdfBuilder builder(Component component) {
      return component.uberFactory().create(PmxBsdfBuilder.class);
    }
  }
}
