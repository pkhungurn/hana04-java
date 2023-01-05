package hana04.base.donothingobject;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.extension.FluentBuilderFactory;
import hana04.base.extension.HanaExtensionFactoryMap;
import hana04.base.extension.HanaObject;
import hana04.base.extension.annotation.HanaCustomizedBuilder;
import hana04.base.extension.annotation.HanaExtensibleSuperclass;
import hana04.base.extension.donothing.DoNothingExtension;
import hana04.base.serialize.HanaSerializable;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;

@Module
public abstract class DoNothingObject__Module {
  @Binds
  @IntoMap
  @ClassKey(DoNothingObjectBuilder.class)
  abstract FluentBuilderFactory provideDoNothingNodeBuilderFactory(DoNothingObjectBuilder.BuilderFactory factory);

  @Binds
  @IntoMap
  @HanaCustomizedBuilder
  @ClassKey(DoNothingObject.class)
  abstract FluentBuilderFactory providCustomizedDoNothingNodeBuilderFactory(
      DoNothingObjectBuilder.BuilderFactory factory);

  @Provides
  @IntoMap
  @HanaExtensibleSuperclass
  @ClassKey(DoNothingObject.class)
  static Class<?> provideDoNothingNodeSuperclass() {
    return HanaObject.class;
  }

  @Binds
  @IntoMap
  @ClassKey(DoNothingObject.class)
  abstract HanaExtensionFactoryMap provideDoNothingNodeExtensionFactoryMap(
      DoNothingObject__ExtensionFactoryMap extensionFactoryMap);

  @Binds
  @IntoMap
  @ClassKey(DoNothingExtension.class)
  abstract DoNothingObject__ExtensionFactory provideDoNothingExtensionFactory(
      DoNothingObject__DoNothingExtension__Factory factory);

  @Binds
  @IntoMap
  @StringKey(DoNothingObject.TYPE_NAME)
  abstract HanaCacheLoader<?> provideDoNothingNodeCacheLoader(DoNothingObject__CacheLoader cacheLoader);

  @Provides
  @IntoMap
  @StringKey(DoNothingObject.TYPE_NAME)
  static TypeReadableSerializer<?> provideDoNothingReadableSerializerByTypeName_0() {
    return new HanaSerializable.ReadableSerializer_<DoNothingObject>();
  }

  @Provides
  @IntoMap
  @StringKey("base." + DoNothingObject.TYPE_NAME)
  static TypeReadableSerializer<?> provideDoNothingReadableSerializerByTypeName_1() {
    return new HanaSerializable.ReadableSerializer_<DoNothingObject>();
  }

  @Provides
  @IntoMap
  @IntKey(DoNothingObject.TYPE_ID)
  static TypeBinarySerializer<?> provideDoNothingBinarySerializerByTypeId() {
    return new HanaSerializable.BinarySerializer_<DoNothingObject>(DoNothingObject.TYPE_ID);
  }

  @Provides
  @IntoMap
  @ClassKey(DoNothingObject.class)
  static TypeReadableSerializer<?> provideDoNothingReadableSerializerByClass() {
    return new HanaSerializable.ReadableSerializer_<DoNothingObject>();
  }

  @Provides
  @IntoMap
  @ClassKey(DoNothingObject.class)
  static TypeBinarySerializer<?> provideDoNothingBinarySerializerByClass() {
    return new HanaSerializable.BinarySerializer_<DoNothingObject>(DoNothingObject.TYPE_ID);
  }

  @Binds
  @IntoMap
  @StringKey(DoNothingObject.TYPE_NAME)
  abstract TypeReadableDeserializer<?> provideNothingNodeReadableDeserializer(
      DoNothingObject__ReadableDeserializer deserializer);

  @Binds
  @IntoMap
  @IntKey(DoNothingObject.TYPE_ID)
  abstract TypeBinaryDeserializer<?> provideDoNothingNodeBinaryDeserializer(
      DoNothingObject__BinaryDeserializer deserializer);
}
