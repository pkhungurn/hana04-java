package hana04.base.serialize.readable;

import com.google.common.base.Preconditions;
import hana04.base.util.TypeUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class JsonParsingUtil {
  private static Optional<List> getChildrenList(Map json) {
    return Optional.ofNullable(json.get("children")).map(childrenObject -> TypeUtil.cast(childrenObject, List.class));
  }

  public static void forEachChildren(
      Map json,
      ReadableDeserializer deserializer,
      BiConsumer<String, Object> childrenConsumer) {
    Optional<List> optionalChildList = getChildrenList(json);
    if (optionalChildList.isEmpty()) {
      return;
    }
    for (Object childrenRawObject : optionalChildList.orElseThrow()) {
      Map childrenRawMap = TypeUtil.cast(childrenRawObject, Map.class);
      String func = TypeUtil.cast(childrenRawMap.get("func"), String.class);
      Object childrenParsed = deserializer.deserialize(childrenRawMap);
      childrenConsumer.accept(func, childrenParsed);
    }
  }

  public static Object getProperty(Map json, String propertyName) {
    Preconditions.checkArgument(json.containsKey(propertyName), "Property '" + propertyName + "' does not exist.");
    Object value = json.get(propertyName);
    Preconditions.checkNotNull(value, "Property '" + propertyName + "' is null.");
    return value;
  }
}
