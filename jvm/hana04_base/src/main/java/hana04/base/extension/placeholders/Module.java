package hana04.base.extension.placeholders;

import dagger.Binds;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.base.extension.HanaExtensible;
import hana04.base.extension.HanaExtensionFactoryMap;
import hana04.base.extension.HanaObject;
import hana04.base.extension.annotation.HanaExtensibleSuperclass;
import hana04.base.extension.donothing.DoNothingExtension;
import hana04.base.extension.donothing.DoNothingExtensionImpl;
import hana04.base.serialize.HanaLateDeserializable;

@dagger.Module
public abstract class Module {
  @Provides
  @IntoMap
  @HanaExtensibleSuperclass
  @ClassKey(HanaObject.class)
  public static Class<?> providesHanaNodeSuperclass() {
    return HanaExtensible.class;
  }

  @Provides
  @IntoMap
  @HanaExtensibleSuperclass
  @ClassKey(HanaLateDeserializable.class)
  public static Class<?> providesHanaLateDeserializableSuperclass() {
    return HanaObject.class;
  }

  @Binds
  @IntoMap
  @ClassKey(HanaExtensible.class)
  public abstract HanaExtensionFactoryMap provide__Extensible__extensionFactoryMap(
      HanaExtensible__HanaExtensionFactoryMap extensionFactoryMap);

  @Binds
  @IntoMap
  @ClassKey(HanaObject.class)
  public abstract HanaExtensionFactoryMap provide__Node__extensionFactoryMap(
      HanaObject__HanaExtensionFactoryMap extensionFactoryMap);

  @Binds
  @IntoMap
  @ClassKey(HanaLateDeserializable.class)
  public abstract HanaExtensionFactoryMap provide__HanaLateDeserializable__entensionFactoryMap(
      HanaLateDeserializable__HanaExtensionFactoryMap extensionFactoryMap);

  @Provides
  @IntoMap
  @ClassKey(DoNothingExtension.class)
  public static HanaExtensible__HanaExtensionFactory provide__Extensible__DoNothingExtensionFactory() {
    return extensible -> new DoNothingExtensionImpl();
  }

  @Provides
  @IntoMap
  @ClassKey(DoNothingExtension.class)
  public static HanaObject__HanaExtensionFactory provide__Node__DoNothingExtensionFactory() {
    return extensible -> new DoNothingExtensionImpl();
  }

  @Provides
  @IntoMap
  @ClassKey(DoNothingExtension.class)
  public static HanaLateDeserializable__HanaExtensionFactory provide__LateDeserializable__DoNothingExtensionFactory() {
    return extensible -> new DoNothingExtensionImpl();
  }
}
