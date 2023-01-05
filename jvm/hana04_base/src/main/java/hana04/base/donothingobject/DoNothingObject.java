package hana04.base.donothingobject;

import com.google.common.collect.ImmutableList;
import hana04.base.extension.AbstractHanaExtensible;
import hana04.base.extension.HanaExtensible;
import hana04.base.extension.HanaExtensionUberFactory;
import hana04.base.extension.HanaObject;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.readable.ReadableSerializer;
import org.msgpack.core.MessagePacker;

import java.util.List;

import static hana04.base.TypeIds.TYPE_ID_DO_NOTHING_OBJECT;

public class DoNothingObject extends AbstractHanaExtensible implements HanaObject {
  public static final int TYPE_ID = TYPE_ID_DO_NOTHING_OBJECT;
  public static final String TYPE_NAME = "DoNothingObject";

  public DoNothingObject(HanaExtensionUberFactory extensionUberFactory) {
    super(extensionUberFactory);
  }

  @Override
  public String getSerializedTypeName() {
    return TYPE_NAME;
  }

  @Override
  public int getSerializedTypeId() {
    return TYPE_ID;
  }

  @Override
  public List getReadableChildrenList(ReadableSerializer serializer) {
    return ImmutableList.of();
  }

  @Override
  public void binarySerializeContent(MessagePacker messagePacker, BinarySerializer binarySerializer) {
    try {
      messagePacker.packMapHeader(0);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Class<? extends HanaExtensible> getExtensibleClass() {
    return DoNothingObject.class;
  }

}
