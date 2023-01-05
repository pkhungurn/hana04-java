package hana04.formats.mmd.common;

import java.util.HashMap;
import java.util.Map;

public enum MmdRigidBodyShapeType {
  Sphere(0),
  Box(1),
  Capsule(2);

  private final int value;

  private MmdRigidBodyShapeType(int value) {
    this.value = value;
  }

  private static final Map<Integer, MmdRigidBodyShapeType> intToTypeMap = new HashMap<Integer, MmdRigidBodyShapeType>();

  static {
    for (MmdRigidBodyShapeType type : MmdRigidBodyShapeType.values()) {
      intToTypeMap.put(type.value, type);
    }
  }

  public static MmdRigidBodyShapeType fromInt(int i) {
    MmdRigidBodyShapeType type = intToTypeMap.get(i);
    if (type == null) {
      throw new RuntimeException("invalid value");
    }
    return type;
  }

  public int getValue() {
    return value;
  }
}
