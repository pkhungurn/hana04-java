package hana04.opengl.wrapper;

public interface GlTexture extends GlObject {
    boolean isDisposed();

    boolean isBound();

    int getId();

    int getTarget();

    void bindTo(GlTextureUnit textureUnit);

    void bind();

    void unbind();

    void use();

    void unuse();

    void useWith(GlTextureUnit textureUnit);

    int getMagFilter();

    void setMagFilter(int value);

    int getMinFilter();

    void setMinFilter(int value);

    int getWrapS();

    void setWrapS(int value);

    int getWrapT();

    void setWrapT(int value);

    int getWrapR();

    void setWrapR(int value);

    boolean isAllocated();
}
