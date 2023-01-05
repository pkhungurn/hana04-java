package hana04.opengl.wrapper;

public class GlAttributeSpec {
    public final String name;
    public final int size;
    public final int type;
    public final boolean normalized;
    public final int stride;
    public final long pointer;

    public GlAttributeSpec(String name, int size, int type, boolean normalized, int stride, long pointer) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.normalized = normalized;
        this.stride = stride;
        this.pointer = pointer;
    }
}
