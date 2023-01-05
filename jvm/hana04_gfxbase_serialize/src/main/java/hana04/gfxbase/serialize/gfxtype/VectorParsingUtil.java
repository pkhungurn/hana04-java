package hana04.gfxbase.serialize.gfxtype;

import hana04.base.util.TypeUtil;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.serialize.PrimitiveParsingUtil;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.List;
import java.util.Map;

public class VectorParsingUtil {
  public static Vector3d parseVector3d(Object value) {
    if (value instanceof List) {
      return toVector3d((List) value);
    } else if (value instanceof String) {
      return toVector3d((String) value);
    } else if (value instanceof Map) {
      return toVector3d((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  public static Point3d parsePoint3d(Object value) {
    if (value instanceof List) {
      return new Point3d(toVector3d((List) value));
    } else if (value instanceof String) {
      return new Point3d(toVector3d((String) value));
    } else if (value instanceof Map) {
      return new Point3d(toVector3d((Map) value));
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Vector3d toVector3d(List list) {
    return new Vector3d(
      PrimitiveParsingUtil.parseDouble(list.get(0)),
      PrimitiveParsingUtil.parseDouble(list.get(1)),
      PrimitiveParsingUtil.parseDouble(list.get(2))
    );
  }

  private static Vector3d toVector3d(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Vector3d(
      Double.valueOf(comps[0]),
      Double.valueOf(comps[1]),
      Double.valueOf(comps[2]));
  }

  private static Vector3d toVector3d(Map map) {
    if (!map.containsKey("x") && !map.containsKey("y") && !map.containsKey("z")) {
      throw new IllegalArgumentException("Map has none of 'x', 'y', nor 'z' key.");
    }
    double x = map.containsKey("x") ? PrimitiveParsingUtil.parseDouble(map.get("x")) : 0.0;
    double y = map.containsKey("y") ? PrimitiveParsingUtil.parseDouble(map.get("y")) : 0.0;
    double z = map.containsKey("z") ? PrimitiveParsingUtil.parseDouble(map.get("z")) : 0.0;
    return new Vector3d(x, y, z);
  }

  public static Vector2d parseVector2d(Object value) {
    if (value instanceof List) {
      return toVector2d((List) value);
    } else if (value instanceof String) {
      return toVector2d((String) value);
    } else if (value instanceof Map) {
      return toVector2d((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Vector2d toVector2d(List list) {
    return new Vector2d(
      PrimitiveParsingUtil.parseDouble(list.get(0)),
      PrimitiveParsingUtil.parseDouble(list.get(1)));
  }

  private static Vector2d toVector2d(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Vector2d(
      Double.valueOf(comps[0]),
      Double.valueOf(comps[1]));
  }

  private static Vector2d toVector2d(Map map) {
    if (!map.containsKey("x") && !map.containsKey("y") && !map.containsKey("z")) {
      throw new IllegalArgumentException("Map has none of 'x', 'y', nor 'z' key.");
    }
    double x = map.containsKey("x") ? PrimitiveParsingUtil.parseDouble(map.get("x")) : 0.0;
    double y = map.containsKey("y") ? PrimitiveParsingUtil.parseDouble(map.get("y")) : 0.0;
    return new Vector2d(x, y);
  }

  public static Vector4d parseVector4d(Object value) {
    if (value instanceof List) {
      return toVector4d((List) value);
    } else if (value instanceof String) {
      return toVector4d((String) value);
    } else if (value instanceof Map) {
      return toVector4d((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  public static Quat4d parseQuat4d(Object value) {
    if (value instanceof List) {
      return new Quat4d(toVector4d((List) value));
    } else if (value instanceof String) {
      return new Quat4d(toVector4d((String) value));
    } else if (value instanceof Map) {
      return new Quat4d(toVector4d((Map) value));
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Vector4d toVector4d(List list) {
    return new Vector4d(
      PrimitiveParsingUtil.parseDouble(list.get(0)),
      PrimitiveParsingUtil.parseDouble(list.get(1)),
      PrimitiveParsingUtil.parseDouble(list.get(2)),
      PrimitiveParsingUtil.parseDouble(list.get(3)));
  }

  private static Vector4d toVector4d(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Vector4d(
      Double.valueOf(comps[0]),
      Double.valueOf(comps[1]),
      Double.valueOf(comps[2]),
      Double.valueOf(comps[3]));
  }

  private static Vector4d toVector4d(Map map) {
    if (!map.containsKey("x") && !map.containsKey("y") && !map.containsKey("z")) {
      throw new IllegalArgumentException("Map has none of 'x', 'y', nor 'z' key.");
    }
    double x = map.containsKey("x") ? PrimitiveParsingUtil.parseDouble(map.get("x")) : 0.0;
    double y = map.containsKey("y") ? PrimitiveParsingUtil.parseDouble(map.get("y")) : 0.0;
    double z = map.containsKey("z") ? PrimitiveParsingUtil.parseDouble(map.get("z")) : 0.0;
    double w = map.containsKey("w") ? PrimitiveParsingUtil.parseDouble(map.get("w")) : 0.0;
    return new Vector4d(x, y, z, w);
  }

  public static Matrix4d parseMatrix4d(Map json) {
    if (json.containsKey("value")) {
      return toMatrix4dFromValueList(TypeUtil.cast(json.get("value"), List.class));
    } else if (json.containsKey("children")) {
      return toMatrix4dFromChildrenList(TypeUtil.cast(json.get("children"), List.class));
    }
    throw new IllegalArgumentException("The given map does not have 'value' nor 'children' key.");
  }

  private static Matrix4d toMatrix4dFromChildrenList(List list) {
    Matrix4d m = Matrix4dUtil.createIdentity();
    for (Object obj : list) {
      Map map = TypeUtil.cast(obj, Map.class);
      String type = TypeUtil.cast(map.get("type"), String.class);
      Matrix4d toMul;
      switch (type) {
        case "FullMatrix":
          toMul = toMatrix4dFromValueList(TypeUtil.cast(map.get("value"), List.class));
          break;
        case "Translate":
          Vector3d delta = VectorParsingUtil.parseVector3d(map.get("value"));
          toMul = Matrix4dUtil.createTranslation(delta.x, delta.y, delta.z);
          break;
        case "Scale":
          Vector3d s = VectorParsingUtil.parseVector3d(map.get("value"));
          toMul = Matrix4dUtil.createScaling(s.x, s.y, s.z);
          break;
        case "Rotate":
          Vector3d axis = VectorParsingUtil.parseVector3d(map.get("axis"));
          axis.normalize();
          double angle = PrimitiveParsingUtil.parseDouble(map.get("angle"));
          toMul = Matrix4dUtil.createRotation(angle, axis.x, axis.y, axis.z);
          break;
        case "LookAt":
          Vector3d origin = VectorParsingUtil.parseVector3d(map.get("origin"));
          Vector3d target = VectorParsingUtil.parseVector3d(map.get("target"));
          Vector3d up = VectorParsingUtil.parseVector3d(map.get("up"));
          toMul = Matrix4dUtil.createLookAtMatrix(
            origin.x, origin.y, origin.z, target.x, target.y, target.z, up.x, up.y, up.z);
          break;
        default:
          throw new IllegalArgumentException("Invalid matrix type: " + type);
      }
      m.mul(toMul);
    }
    return m;
  }

  private static Matrix4d toMatrix4dFromValueList(List list) {
    Matrix4d m = new Matrix4d();
    if (list.size() == 16) {
      m.m00 = PrimitiveParsingUtil.parseDouble(list.get(0));
      m.m01 = PrimitiveParsingUtil.parseDouble(list.get(1));
      m.m02 = PrimitiveParsingUtil.parseDouble(list.get(2));
      m.m03 = PrimitiveParsingUtil.parseDouble(list.get(3));

      m.m10 = PrimitiveParsingUtil.parseDouble(list.get(4));
      m.m11 = PrimitiveParsingUtil.parseDouble(list.get(5));
      m.m12 = PrimitiveParsingUtil.parseDouble(list.get(6));
      m.m13 = PrimitiveParsingUtil.parseDouble(list.get(7));

      m.m20 = PrimitiveParsingUtil.parseDouble(list.get(8));
      m.m21 = PrimitiveParsingUtil.parseDouble(list.get(9));
      m.m22 = PrimitiveParsingUtil.parseDouble(list.get(10));
      m.m23 = PrimitiveParsingUtil.parseDouble(list.get(11));

      m.m30 = PrimitiveParsingUtil.parseDouble(list.get(12));
      m.m31 = PrimitiveParsingUtil.parseDouble(list.get(13));
      m.m32 = PrimitiveParsingUtil.parseDouble(list.get(14));
      m.m33 = PrimitiveParsingUtil.parseDouble(list.get(15));
    } else if (list.size() == 4) {
      List row0 = TypeUtil.cast(list.get(0), List.class);
      List row1 = TypeUtil.cast(list.get(1), List.class);
      List row2 = TypeUtil.cast(list.get(2), List.class);
      List row3 = TypeUtil.cast(list.get(3), List.class);

      m.m00 = PrimitiveParsingUtil.parseDouble(row0.get(0));
      m.m01 = PrimitiveParsingUtil.parseDouble(row0.get(1));
      m.m02 = PrimitiveParsingUtil.parseDouble(row0.get(2));
      m.m03 = PrimitiveParsingUtil.parseDouble(row0.get(3));

      m.m10 = PrimitiveParsingUtil.parseDouble(row1.get(0));
      m.m11 = PrimitiveParsingUtil.parseDouble(row1.get(1));
      m.m12 = PrimitiveParsingUtil.parseDouble(row1.get(2));
      m.m13 = PrimitiveParsingUtil.parseDouble(row1.get(3));

      m.m20 = PrimitiveParsingUtil.parseDouble(row2.get(0));
      m.m21 = PrimitiveParsingUtil.parseDouble(row2.get(1));
      m.m22 = PrimitiveParsingUtil.parseDouble(row2.get(2));
      m.m23 = PrimitiveParsingUtil.parseDouble(row2.get(3));

      m.m30 = PrimitiveParsingUtil.parseDouble(row3.get(0));
      m.m31 = PrimitiveParsingUtil.parseDouble(row3.get(1));
      m.m32 = PrimitiveParsingUtil.parseDouble(row3.get(2));
      m.m33 = PrimitiveParsingUtil.parseDouble(row3.get(3));
    } else {
      throw new IllegalArgumentException("list must be of length 4 or 16.");
    }
    return m;
  }

  public static Point3i parsePoint3i(Object value) {
    if (value instanceof List) {
      return toPoint3i((List) value);
    } else if (value instanceof String) {
      return toPoint3i((String) value);
    } else if (value instanceof Map) {
      return toPoint3i((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Point3i toPoint3i(List list) {
    return new Point3i(
      PrimitiveParsingUtil.parseInteger(list.get(0)),
      PrimitiveParsingUtil.parseInteger(list.get(1)),
      PrimitiveParsingUtil.parseInteger(list.get(2)));
  }

  private static Point3i toPoint3i(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Point3i(
      Integer.parseInt(comps[0]),
      Integer.parseInt(comps[1]),
      Integer.parseInt(comps[2]));
  }

  private static Point3i toPoint3i(Map map) {
    if (!map.containsKey("x") && !map.containsKey("y") && !map.containsKey("z")) {
      throw new IllegalArgumentException("Map has none of 'x', 'y', nor 'z' key.");
    }
    int x = map.containsKey("x") ? PrimitiveParsingUtil.parseInteger(map.get("x")) : 0;
    int y = map.containsKey("y") ? PrimitiveParsingUtil.parseInteger(map.get("y")) : 0;
    int z = map.containsKey("z") ? PrimitiveParsingUtil.parseInteger(map.get("z")) : 0;
    return new Point3i(x, y, z);
  }

}
