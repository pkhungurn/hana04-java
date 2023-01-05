
package hana04.formats.x;

public class XPrimitiveType implements XType {
  private XPrimitiveType() {
    // NO-OP
  }

  public static final XPrimitiveType BYTE = new XPrimitiveType();
  public static final XPrimitiveType CHAR = new XPrimitiveType();
  public static final XPrimitiveType DWORD = new XPrimitiveType();
  public static final XPrimitiveType DOUBLE = new XPrimitiveType();
  public static final XPrimitiveType FLOAT = new XPrimitiveType();
  public static final XPrimitiveType STRING = new XPrimitiveType();
  public static final XPrimitiveType UCHAR = new XPrimitiveType();
  public static final XPrimitiveType WORD = new XPrimitiveType();
}
