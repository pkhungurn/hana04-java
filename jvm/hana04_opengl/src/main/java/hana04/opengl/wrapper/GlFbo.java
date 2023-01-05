package hana04.opengl.wrapper;

public interface GlFbo extends GlObject {
  void bind();

  void unbind();

  boolean isBound();

  int getId();

  GlTexture getColorAttachment(int index);

  GlTexture getDepthAttachment();

  GlTexture getStencilAttachment();

  void attachColorBuffer(int index, GlTexture texture);

  void detachColorBuffer(int index);

  void attachDepthBuffer(GlTexture texture);

  void detachDepthBuffer();

  void attachStencilBuffer(GlTexture texture);

  void detachStencilBuffer();

  void drawToNone();

  void readFromNone();

  void drawTo(int start, int count);

  void readFrom(int index);

  void drawTo(GlTexture t0);

  void drawTo(GlTexture t0, GlTexture t1);

  void drawTo(GlTexture t0, GlTexture t1, GlTexture t2);

  void drawTo(GlTexture t0, GlTexture t1, GlTexture t2, GlTexture t3);

  void detachAllColorBuffers();

  void detachAll();
}
