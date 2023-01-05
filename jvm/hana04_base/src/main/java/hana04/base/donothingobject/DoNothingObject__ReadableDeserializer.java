package hana04.base.donothingobject;

import hana04.base.extension.HanaExtensionUberFactory;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class DoNothingObject__ReadableDeserializer implements TypeReadableDeserializer<DoNothingObject> {
  private HanaExtensionUberFactory extensionUberFactory;

  @Inject
  public DoNothingObject__ReadableDeserializer(HanaExtensionUberFactory extensionUberFactory) {
    this.extensionUberFactory = extensionUberFactory;
  }

  @Override
  public DoNothingObject deserialize(Map json, ReadableDeserializer deserializer) {
    DoNothingObject result = new DoNothingObject(extensionUberFactory);
    deserializer.deserializeExtensions(json, result);
    return result;
  }

  @Override
  public Class<DoNothingObject> getSerializedClass() {
    return DoNothingObject.class;
  }
}
