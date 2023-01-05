package hana04.base.changeprop.util;

import com.google.common.collect.ImmutableList;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;

public class UnwrapVv<T> extends DerivedVersionedValue<T> {
  public UnwrapVv(VersionedValue<Wrapped<T>> wrapped, HanaUnwrapper unwrapper) {
    super(
      ImmutableList.of(wrapped),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> wrapped.value().unwrap(unwrapper));
  }
}
