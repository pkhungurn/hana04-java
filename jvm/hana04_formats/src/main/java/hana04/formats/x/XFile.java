package hana04.formats.x;

import java.util.ArrayList;
import java.util.List;

public class XFile {
  private final List<XTemplate> templates = new ArrayList<XTemplate>();
  private final List<Object> data = new ArrayList<Object>();
  private String versionString;
  private XFileFloatSize floatSize;
  private XFileFormat format;
  private String directory = ".";

  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public XFileFloatSize getFloatSize() {
    return floatSize;
  }

  public void setFloatSize(XFileFloatSize floatSize) {
    this.floatSize = floatSize;
  }

  public XFileFormat getFormat() {
    return format;
  }

  public void setFormat(XFileFormat format) {
    this.format = format;
  }

  public XTemplate lookupTemplateByName(String name) {
    for (int i = 0; i < templates.size(); i++) {
      if (templates.get(i).name.equals(name)) {
        return templates.get(i);
      }
    }
    return null;
  }

  public void addTemplate(XTemplate template) {
    templates.add(template);
  }

  public void addData(Object datum) {
    data.add(datum);
  }

  public List<XTemplate> getTemplates() {
    return templates;
  }

  public List<Object> getData() {
    return data;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String dir) {
    this.directory = dir;
  }
}
