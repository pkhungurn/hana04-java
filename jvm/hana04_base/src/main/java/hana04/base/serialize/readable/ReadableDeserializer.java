package hana04.base.serialize.readable;

import com.google.common.base.Preconditions;
import hana04.base.extension.HanaExtensible;
import hana04.base.serialize.HanaLateDeserializable;
import hana04.base.serialize.HanaSerializable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReadableDeserializer {
  private final Optional<String> fileName;
  private final Map<String, HanaSerializable> idToNode = new HashMap<>();
  private final TypeNameToReadableDeserializerMap typeNameToDeserializer;

  ReadableDeserializer(Optional<String> fileName, TypeNameToReadableDeserializerMap typeNameToDeserializer) {
    this.fileName = fileName;
    this.typeNameToDeserializer = typeNameToDeserializer;
  }

  public <T> T deserialize(Map json) {
    String typeName = Preconditions.checkNotNull((String) json.get("type"),
      "The given JSON object does not attribute 'type'.");
    if (typeName.equals(ReadableSerializer.LOOK_UP_TYPE_NAME)) {
      String id = (String) json.get("id");
      return (T) idToNode.get(id);
    }
    Object result = getDeserializer(typeName).deserialize(json, this);
    if (result instanceof HanaSerializable && json.containsKey("id")) {
      HanaSerializable node = (HanaSerializable) result;
      String id = (String) json.get("id");
      idToNode.put(id, node);
    }
    return (T) result;
  }

  private TypeReadableDeserializer getDeserializer(String typeName) {
    return Preconditions.checkNotNull(typeNameToDeserializer.get(typeName),
      "TypeReadableDeserializer for type " + typeName + " does not exist");
  }

  public Optional<String> getFileName() {
    return fileName;
  }

  @Singleton
  public static class Factory {
    private final TypeNameToReadableDeserializerMap typeNameToDeserializer;

    @Inject
    public Factory(TypeNameToReadableDeserializerMap typeNameToDeserializer) {
      this.typeNameToDeserializer = typeNameToDeserializer;
    }

    public ReadableDeserializer create() {
      return new ReadableDeserializer(Optional.empty(), typeNameToDeserializer);
    }

    public ReadableDeserializer create(String fileName) {
      return new ReadableDeserializer(Optional.of(fileName), typeNameToDeserializer);
    }
  }

  public void deserializeExtensions(Map json, HanaExtensible extensible) {
    if (!json.containsKey("extensions")) {
      return;
    }
    Preconditions.checkArgument(json.get("extensions") instanceof List,
      "The 'extensions' attribute of the given JSON object is not a List.");
    List extensions = (List)json.get("extensions");
    for(Object extension : extensions) {
      Preconditions.checkArgument(extension instanceof Map,
        "An item in the 'extensions' list of the given JSON object is not a Map");
      Map extensionMap = (Map)extension;
      String typeName = Preconditions.checkNotNull((String) extensionMap.get("type"),
        "The Map object inside the 'extensions' list does not contain the attribute 'type'.");
      TypeReadableDeserializer deserializer = getDeserializer(typeName);
      Object ex = extensible.getExtension(deserializer.getSerializedClass());
      Preconditions.checkArgument(ex instanceof HanaLateDeserializable,
        "A serialized extension is not a LateDeserializable");
      HanaLateDeserializable lateDeserializable = (HanaLateDeserializable)ex;
      lateDeserializable.deserialize(extensionMap, this);
    }
  }
}
