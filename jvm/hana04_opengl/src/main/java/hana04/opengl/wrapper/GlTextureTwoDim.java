package hana04.opengl.wrapper;

import java.nio.Buffer;

public interface GlTextureTwoDim extends GlTexture {
    int getWidth();

    int getHeight();

    void setData(int width, int height, int format, int type, Buffer buffer);

    default void allocate(int width, int height) {
        allocate(width, height, GlConstants.GL_RGBA, GlConstants.GL_FLOAT);
    }

    default void allocate(int width, int height, int format, int type) {
        setData(width, height, format, type, null);
    }

    boolean hasMipmap();

    void getData(int format, int type, Buffer buffer);
}
