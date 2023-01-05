package hana04.shakuyaku.surface.mmd.util;

import hana04.apt.annotation.HanaDeclareLateDeserializable;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.serialize.HanaLateDeserializable;
import hana04.shakuyaku.TypeIds;

import java.util.List;

@HanaDeclareLateDeserializable(
    parent = HanaLateDeserializable.class,
    typeId = TypeIds.TYPE_ID_MATERIAL_AMBIENT_USAGE_DATA,
    typeNames = {"shakuyaku.MaterialAmbientUsageData", "MaterialAmbientUsageData"})
public interface MaterialAmbientUsageData extends HanaLateDeserializable {
  @HanaProperty(1)
  Variable<String> ambientMode();

  @HanaProperty(2)
  Variable<List<String>> materialWithAmbientOpting();
}
