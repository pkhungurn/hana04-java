package hana04.shakuyaku.texture.twodim.image;

import hana04.apt.annotation.HanaDeclareLateDeserializable;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.serialize.HanaLateDeserializable;
import hana04.shakuyaku.TypeIds;

@HanaDeclareLateDeserializable(
    parent = HanaLateDeserializable.class,
    typeId = TypeIds.TYPE_ID_IMAGE_LOADING_FAIL_BEHAVIOR_DATA,
    typeNames = {"shakuyaku.ImageLoadingFailBehaviorData", "ImageLoadingFailBehaviorData"})
public interface ImageLoadingFailBehaviorData extends HanaLateDeserializable {
  @HanaProperty(1)
  Variable<Boolean> defaultToBlackImageIfLoadingFail();
}
