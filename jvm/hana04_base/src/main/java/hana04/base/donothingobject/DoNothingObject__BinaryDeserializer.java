package hana04.base.donothingobject;

import com.google.common.base.Preconditions;
import hana04.base.extension.HanaExtensionUberFactory;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class DoNothingObject__BinaryDeserializer implements TypeBinaryDeserializer<DoNothingObject> {
  private HanaExtensionUberFactory extensionUberFactory;

  @Inject
  public DoNothingObject__BinaryDeserializer(HanaExtensionUberFactory extensionUberFactory) {
    this.extensionUberFactory = extensionUberFactory;
  }

  @Override
  public DoNothingObject deserialize(Value value, BinaryDeserializer deserializer) {
    DoNothingObject result = new DoNothingObject(extensionUberFactory);
    Preconditions.checkArgument(value.isMapValue());
    Map<Integer, Value> map = BinaryDeserializer.convertToIntMap(value.asMapValue());
    if (!map.containsKey(BinarySerializer.EXTENSION_TAG)) {
      return result;
    }
    Preconditions.checkArgument(map.get(BinarySerializer.EXTENSION_TAG).isArrayValue());
    deserializer.deserializeExtensions(map.get(BinarySerializer.EXTENSION_TAG).asArrayValue(), result);
    return result;
  }

  @Override
  public Class<?> getSerializedClass() {
    return DoNothingObject.class;
  }
}
