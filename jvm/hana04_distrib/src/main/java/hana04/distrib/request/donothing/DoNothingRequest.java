package hana04.distrib.request.donothing;

import com.google.common.collect.ImmutableList;
import hana04.base.extension.AbstractHanaExtensible;
import hana04.base.extension.HanaExtensible;
import hana04.base.extension.HanaExtensionUberFactory;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.distrib.TypeIds;
import hana04.distrib.request.Request;
import org.msgpack.core.MessagePacker;

import java.util.List;
import java.util.UUID;

public class DoNothingRequest extends AbstractHanaExtensible implements Request {
  public static final int TYPE_ID = TypeIds.TYPE_ID_DO_NOTHING_REQUEST;
  public static final String TYPE_NAME = "DoNothingRequest";

  private final UUID uuid;

  public DoNothingRequest(HanaExtensionUberFactory extensionUberFactory) {
    super(extensionUberFactory);
    uuid = UUID.randomUUID();
  }

  @Override
  protected Class<? extends HanaExtensible> getExtensibleClass() {
    return DoNothingRequest.class;
  }

  @Override
  public UUID uuid() {
    return uuid;
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
}
