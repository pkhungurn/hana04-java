package hana04.formats.mmd.common;

import java.util.HashMap;
import java.util.Map;

public enum MmdRigidBodyType {
  FollowBone(0),
  Physics(1),
  PhysicsWithBonePosition(2);

  private final int value;

  MmdRigidBodyType(int value) {
    this.value = value;
  }

  private static final Map<Integer, MmdRigidBodyType> intToTypeMap = new HashMap<Integer, MmdRigidBodyType>();

  static {
    for (MmdRigidBodyType type : MmdRigidBodyType.values()) {
      intToTypeMap.put(type.value, type);
    }
  }

  public static MmdRigidBodyType fromInt(int i) {
    MmdRigidBodyType type = intToTypeMap.get(i);
    if (type == null) {
      throw new RuntimeException("invalid value");
    }
    return type;
  }

  public int getValue() {
    return value;
  }
}
