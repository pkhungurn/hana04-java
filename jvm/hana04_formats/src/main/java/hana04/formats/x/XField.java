
package hana04.formats.x;

public class XField {
  protected String name;
  protected XType type;

  public XField() {
    name = "";
    type = null;
  }

  public XField(String name, XType type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public XType getType() {
    return type;
  }

  public void setType(XType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof XField))
      return false;
    else {
      XField other = (XField) o;
      return name.equals(other.name) && type.equals(other.type);
    }
  }
}                        
