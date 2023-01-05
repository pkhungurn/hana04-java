package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;

public class LwjglVboTarget {
    private final int constant;
    private static LwjglVbo boundVbo = null;

    public static LwjglVboTarget ARRAY_BUFFER = new LwjglVboTarget(GlConstants.GL_ARRAY_BUFFER);
    public static LwjglVboTarget ELEMENT_ARRAY_BUFFER = new LwjglVboTarget(GlConstants.GL_ELEMENT_ARRAY_BUFFER);

    private LwjglVboTarget(int constant) {
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }

    public LwjglVbo getBoundVbo() {
        return boundVbo;
    }

    public void setBoundVbo(LwjglVbo vbo) {
        boundVbo = vbo;
    }

    public void unbindVbo() {
        if (boundVbo != null) {
            boundVbo.unbind();
        }
    }

}
