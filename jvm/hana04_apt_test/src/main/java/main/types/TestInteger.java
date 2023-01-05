package main.types;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.extension.HanaObject;
import main.TypeIds;

@HanaDeclareObject(
    parent = HanaObject.class,
    typeId = TypeIds.TYPE_ID_MAIN_TYPES_TEST_INTEGER,
    typeNames = {"main.types.TestInteger"})
public interface TestInteger extends HanaObject {
  @HanaProperty(1)
  Integer intConst();
}
