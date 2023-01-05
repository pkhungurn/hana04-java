package main;

import hana04.apt.annotation.HanaDeclareLateDeserializable;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.serialize.HanaLateDeserializable;

@HanaDeclareLateDeserializable(
    parent = HanaLateDeserializable.class,
    typeId = TypeIds.TYPE_ID_CCC,
    typeNames = {"base.Ccc", "Ccc"})
public interface Ccc extends HanaLateDeserializable {
  @HanaProperty(1)
  Variable<Integer> intVar();

  @HanaProperty(2)
  Variable<Float> floatValue();
}
