package hana04.gfxbase.serialize.gfxtype;

import hana04.gfxbase.gfxtype.Aabb2d;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.serialize.PrimitiveParsingUtil;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class AabbParsingUtil {
  public static Aabb2d parseAabb2d(Object value) {
    if (value instanceof List) {
      return toAabb2d((List) value);
    } else if (value instanceof String) {
      return toAabb2d((String) value);
    } else if (value instanceof Map) {
      return toAabb2d((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Aabb2d toAabb2d(List list) {
    return new Aabb2d(
      new Vector2d(
        PrimitiveParsingUtil.parseDouble(list.get(0)),
        PrimitiveParsingUtil.parseDouble(list.get(1))),
      new Vector2d(
        PrimitiveParsingUtil.parseDouble(list.get(2)),
        PrimitiveParsingUtil.parseDouble(list.get(3))));
  }

  private static Aabb2d toAabb2d(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Aabb2d(
      new Vector2d(
        Double.parseDouble(comps[0]),
        Double.parseDouble(comps[1])),
      new Vector2d(
        Double.parseDouble(comps[2]),
        Double.parseDouble(comps[3])));
  }

  private static Aabb2d toAabb2d(Map map) {
    if (!map.containsKey("pMin") || !map.containsKey("pMax")) {
      throw new IllegalArgumentException("Map does not have 'pMin' and 'pMax'.");
    }
    Vector2d pMin = VectorParsingUtil.parseVector2d(map.get("pMin"));
    Vector2d pMax = VectorParsingUtil.parseVector2d(map.get("pMax"));
    return new Aabb2d(pMin, pMax);
  }

  public static Aabb3d parseAabb3d(Object value) {
    if (value instanceof List) {
      return toAabb3d((List) value);
    } else if (value instanceof String) {
      return toAabb3d((String) value);
    } else if (value instanceof Map) {
      return toAabb3d((Map) value);
    } else {
      throw new IllegalArgumentException("Cannot convert the given value to a vector.");
    }
  }

  private static Aabb3d toAabb3d(List list) {
    return new Aabb3d(
      new Vector3d(
        PrimitiveParsingUtil.parseDouble(list.get(0)),
        PrimitiveParsingUtil.parseDouble(list.get(1)),
        PrimitiveParsingUtil.parseDouble(list.get(2))),
      new Vector3d(
        PrimitiveParsingUtil.parseDouble(list.get(3)),
        PrimitiveParsingUtil.parseDouble(list.get(4)),
        PrimitiveParsingUtil.parseDouble(list.get(5))));
  }

  private static Aabb3d toAabb3d(String value) {
    String[] comps = value.split("[\\s,]+");
    return new Aabb3d(
      new Vector3d(
        Double.parseDouble(comps[0]),
        Double.parseDouble(comps[1]),
        Double.parseDouble(comps[2])),
      new Vector3d(
        Double.parseDouble(comps[3]),
        Double.parseDouble(comps[4]),
        Double.parseDouble(comps[5])));
  }

  private static Aabb3d toAabb3d(Map map) {
    if (!map.containsKey("pMin") || !map.containsKey("pMax")) {
      throw new IllegalArgumentException("Map does not have 'pMin' and 'pMax'.");
    }
    Vector3d pMin = VectorParsingUtil.parseVector3d(map.get("pMin"));
    Vector3d pMax = VectorParsingUtil.parseVector3d(map.get("pMax"));
    return new Aabb3d(pMin, pMax);
  }
}
