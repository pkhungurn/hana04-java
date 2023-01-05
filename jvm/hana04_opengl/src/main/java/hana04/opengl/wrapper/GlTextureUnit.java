package hana04.opengl.wrapper;

public interface GlTextureUnit {
  public void activate();

  public void bindToUniform(GlUniform target);

  public void bindTexture(GlTexture texture);

  public void unbindTexture(GlTexture texture);

  public void unbindTexture();

  public int getIndex();

  public int getId();

  public GlTexture getBoundTexture();
}
