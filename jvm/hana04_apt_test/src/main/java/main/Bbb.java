package main;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;

import java.util.List;
import java.util.Optional;

@HanaDeclareObject(
  parent = HanaObject.class,
  typeId = TypeIds.TYPE_ID_BBB,
  typeNames = {"base.Bbb", "Bbb"})
public interface Bbb extends HanaObject {
  @HanaProperty(1)
  Aaa aaaConst();

  @HanaProperty(2)
  Variable<Aaa> aaaVar();

  @HanaProperty(3)
  Wrapped<Aaa> aaaWrappedConst();

  @HanaProperty(4)
  Variable<Wrapped<Aaa>> aaaWrappedVar();

  @HanaProperty(5)
  List<Aaa> aaaListConst();

  @HanaProperty(6)
  Variable<List<Aaa>> aaaListVar();

  @HanaProperty(7)
  List<Wrapped<Aaa>> aaaWrappedListConst();

  @HanaProperty(8)
  Variable<List<Wrapped<Aaa>>> aaaWrappedListVar();

  @HanaProperty(9)
  Optional<Aaa> aaaOptionalConst();

  @HanaProperty(10)
  Variable<Optional<Aaa>> aaaOptionalVar();

  @HanaProperty(11)
  Optional<Wrapped<Aaa>> aaaWrappedOptionalConst();

  @HanaProperty(12)
  Variable<Optional<Wrapped<Aaa>>> aaaWrappedOptionalVar();
}
