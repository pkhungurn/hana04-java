package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTexture3D;

import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;

public class LwjglTexture3D extends LwjglTextureThreeDim implements GlTexture3D {
    public LwjglTexture3D() {
        super(GL_TEXTURE_3D, GlConstants.GL_RED);
    }

    public LwjglTexture3D(int internalFormat) {
        super(GL_TEXTURE_3D, internalFormat);
    }
}
