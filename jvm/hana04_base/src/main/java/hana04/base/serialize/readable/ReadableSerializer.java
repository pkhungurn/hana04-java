package hana04.base.serialize.readable;

import com.cedarsoftware.util.io.JsonWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import hana04.base.serialize.HanaSerializable;
import hana04.base.util.TextIo;
import hana04.base.util.TypeUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ReadableSerializer {
  public static final String LOOK_UP_TYPE_NAME = "LookUp";

  private final Optional<String> fileName;
  private final TypeNameToReadableSerializerMap typeNameToSerializer;
  private final ClassToReadableSerializerMap classToSerializer;
  private final Map<HanaSerializable, UUID> objToUuid = new HashMap<>();

  public ReadableSerializer(Optional<String> fileName,
      TypeNameToReadableSerializerMap typeNameToSerializer,
      ClassToReadableSerializerMap classToSerializer) {
    this.fileName = fileName;
    this.typeNameToSerializer = typeNameToSerializer;
    this.classToSerializer = classToSerializer;
  }

  public Map<String, ?> serialize(Object obj, String func) {
    Map result = serialize(obj);
    result.put("func", func);
    return result;
  }

  public Map serialize(Object obj) {
    /*
    if (obj instanceof Direct) {
      return serialize(TypeUtil.cast(obj, Direct.class).value);
    } else if (obj instanceof Variable) {
      return serialize(TypeUtil.cast(obj, Variable.class).value());
    }
     */
    if (objToUuid.containsKey(obj)) {
      HashMap map = new HashMap();
      map.put("type", LOOK_UP_TYPE_NAME);
      map.put("id", objToUuid.get(obj).toString());
      return map;
    }
    TypeReadableSerializer serializer;
    if (obj instanceof HanaSerializable) {
      serializer = typeNameToSerializer.get(TypeUtil.cast(obj, HanaSerializable.class).getSerializedTypeName());
    } else {
      serializer = classToSerializer.get(obj.getClass());
    }
    if (serializer == null) {
      throw new RuntimeException("No serializer for type " + obj.getClass().getCanonicalName());
    }
    Map result = serializer.serialize(obj, this);
    if (obj instanceof HanaSerializable) {
      UUID uuid = UUID.randomUUID();
      objToUuid.put((HanaSerializable) obj, uuid);
      result.put("id", uuid.toString());
    }
    return result;
  }

  public Optional<String> getFileName() {
    return fileName;
  }

  @Singleton
  public static class Factory {
    private final TypeNameToReadableSerializerMap typeNameToSerializer;
    private final ClassToReadableSerializerMap classToSerializer;

    @Inject
    public Factory(TypeNameToReadableSerializerMap typeNameToSerializer,
        ClassToReadableSerializerMap classToSerializer) {
      this.typeNameToSerializer = typeNameToSerializer;
      this.classToSerializer = classToSerializer;
    }

    public ReadableSerializer create() {
      return new ReadableSerializer(Optional.empty(), typeNameToSerializer, classToSerializer);
    }

    public ReadableSerializer create(String fileName) {
      return new ReadableSerializer(Optional.of(fileName), typeNameToSerializer, classToSerializer);
    }
  }

  public static void save(Path path, Map json) {
    save(path, json, /* prettyPrint= */ true);
  }

  public static void save(Path path, Map json, boolean prettyPrint) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      if (prettyPrint) {
        TextIo.writeTextFile(
            path,
            JsonWriter.formatJson(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)));
      } else {
        TextIo.writeTextFile(path, mapper.writeValueAsString(json));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
