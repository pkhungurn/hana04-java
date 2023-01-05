
package hana04.formats.x;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XTemplate implements XType {
  protected UUID uuid;
  protected String name;
  protected final List<XField> fields = new ArrayList<XField>();

  public XTemplate(String name, UUID uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof XTemplate)) {
      return false;
    } else {
      XTemplate other = (XTemplate) o;
      return name.equals(other.name) && uuid.equals(other.uuid);
    }
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
    hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
    return hash;
  }

  public void addField(XField field) {
    fields.add(field);
  }

  public XField getField(int index) {
    return fields.get(index);
  }

  public List<XField> getFields() {
    return fields;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }
}
