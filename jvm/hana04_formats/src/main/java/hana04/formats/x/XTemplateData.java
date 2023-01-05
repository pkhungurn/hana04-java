
package hana04.formats.x;

import java.util.ArrayList;
import java.util.List;

public class XTemplateData {
  protected XTemplate template;
  protected final List<Object> values = new ArrayList<Object>();

  public XTemplateData(XTemplate template) {
    this.template = template;
  }

  public XTemplateData(XTemplate template, List<? extends Object> values) {
    this.template = template;
    this.values.addAll(values);
  }

  public XTemplate getTemplate() {
    return template;
  }

  public List<Object> getValues() {
    return values;
  }

  public Object getValue(int index) {
    return values.get(index);
  }

  public void addValue(Object value) {
    values.add(value);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof XTemplateData)) {
      return false;
    } else {
      XTemplateData other = (XTemplateData) o;
      return template.equals(other.template) &&
        values.equals(other.values);
    }
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + (this.template != null ? this.template.hashCode() : 0);
    hash = 47 * hash + (this.values != null ? this.values.hashCode() : 0);
    return hash;
  }
}
