package hana04.opengl.wrapper;

public interface GlAttribute {
  String getName();

  int getIndex();

  int getSize();

  int getType();

  void setEnabled(boolean enabled);

  boolean isEnabled();

  void setup(int size, int type, boolean normalized, int stride, long pointer);

  void setup(GlAttributeSpec spec);
}
