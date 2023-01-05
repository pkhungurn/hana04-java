package hana04.serialize.filesystem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.filesystem.FilePath;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.base.util.StringSerializationUtil;
import hana04.base.util.TypeUtil;
import hana04.serialize.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class FilePathSerialization {
  public static final String TYPE_NAME = "FilePath";

  @HanaDeclareReadableSerializerByClass(FilePath.class)
  public static class ReadableSerializer implements TypeReadableSerializer<FilePath> {
    @Inject
    public ReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(FilePath obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.builder()
          .put("type", TYPE_NAME)
          .put("relative", obj.saveAsRelative)
          .put("value", obj.getSerializedPath(serializer.getFileName()))
          .build());
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<FilePath> {
    @Inject
    public ReadableDeserializer() {
      // NO-OP
    }

    @Override
    public FilePath deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      boolean relative = TypeUtil.cast(json.get("relative"), Boolean.class);
      String rawPath = TypeUtil.cast(json.get("value"), String.class);
      String pathToStore = relative ? FilePath.computePathToStore(rawPath, deserializer.getFileName()) : rawPath;
      return new FilePath(relative, pathToStore);
    }

    @Override
    public Class<FilePath> getSerializedClass() {
      return FilePath.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(FilePath.class)
  public static class FilePathBinarySerializer implements TypeBinarySerializer<FilePath> {
    @Inject
    public FilePathBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_FILE_PATH;
    }

    @Override
    public void serialize(
        FilePath obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        boolean isRelative = obj.saveAsRelative;
        String fileName = obj.getSerializedPath(serializer.getFileName());
        messagePacker.packArrayHeader(2);
        messagePacker.packBoolean(isRelative);
        StringSerializationUtil.packString(messagePacker, fileName);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_FILE_PATH)
  public static class FilePathBinaryDeserializer implements TypeBinaryDeserializer<FilePath> {
    @Inject
    public FilePathBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return FilePath.class;
    }

    @Override
    public FilePath deserialize(Value value, BinaryDeserializer deserializer) {
      ArrayValue arrayValue = value.asArrayValue();
      boolean isRelative = arrayValue.get(0).asBooleanValue().getBoolean();
      String rawPath = arrayValue.get(1).asStringValue().asString();
      String absPath = isRelative ? FilePath.computePathToStore(rawPath, deserializer.getFileName()) : rawPath;
      return new FilePath(isRelative, absPath);
    }
  }
}
