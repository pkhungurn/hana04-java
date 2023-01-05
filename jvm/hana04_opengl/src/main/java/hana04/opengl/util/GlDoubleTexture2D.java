package hana04.opengl.util;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlObject;
import hana04.opengl.wrapper.GlTexture2D;
import hana04.opengl.wrapper.GlWrapper;

public class GlDoubleTexture2D implements GlObject {
    private GlTexture2D[] buffers;
    private int readIndex = 0;

    public GlDoubleTexture2D(GlWrapper gl) {
        this(gl, GlConstants.GL_RGBA32F);
    }


    public GlDoubleTexture2D(GlWrapper gl, int internalFormat) {
        buffers = new GlTexture2D[2];
        for (int i = 0; i < 2; i++) {
            buffers[i] = gl.createTexture2D(internalFormat, false);
        }
    }

    public void allocate(int width, int height) {
        for (int i = 0; i < 2; i++) {
            buffers[i].allocate(width, height);
        }
    }

    public void allocate(int width, int height, int format, int type) {
        for (int i = 0; i < 2; i++) {
            buffers[i].allocate(width, height, format, type);
        }
    }

    public void swap() {
        readIndex = (readIndex + 1) % 2;
    }

    public GlTexture2D getReadBuffer() {
        return buffers[readIndex];
    }

    public GlTexture2D getWriteBuffer() {
        return buffers[(readIndex+1)%2];
    }

    public int getWidth() {
        return buffers[0].getWidth();
    }

    public int getHeight() {
        return buffers[0].getHeight();
    }

    @Override
    public void disposeGl() {
        buffers[0].disposeGl();
        buffers[1].disposeGl();
    }
}
