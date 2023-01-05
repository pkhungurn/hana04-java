package hana04.formats.mmd.common;

import java.util.HashMap;
import java.util.Map;

public enum MmdMorphPanel {
  Unknown(0),
  EyeBrows(1),
  Eyes(2),
  Lips(3),
  Other(4);

  private final int value;

  MmdMorphPanel(int value) {
    this.value = value;
  }

  private static final Map<Integer, MmdMorphPanel> intToTypeMap = new HashMap<Integer, MmdMorphPanel>();

  static {
    for (MmdMorphPanel type : MmdMorphPanel.values()) {
      intToTypeMap.put(type.value, type);
    }
  }

  public static MmdMorphPanel fromInt(int i) {
    MmdMorphPanel type = intToTypeMap.get(i);
    if (type == null) {
      throw new RuntimeException("invalid value");
    }
    return type;
  }

  public int getValue() {
    return value;
  }
}
