package main.types;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.extension.HanaObject;
import main.TypeIds;

import java.util.Map;

@HanaDeclareObject(
    parent = HanaObject.class,
    typeId = TypeIds.TYPE_ID_MAIN_TYPES_TEST_MAP,
    typeNames = {"main.types.TestMap"})
public interface TestMap extends HanaObject {
  @HanaProperty(1)
  Map<String, Double> stringDoubleMap();
}
