package hana04.base.changeprop.util;

import com.google.common.collect.ImmutableList;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;

import java.util.Optional;

public class UnwrapVvOptional<T> extends DerivedVersionedValue<Optional<T>> {
  public UnwrapVvOptional(
    VersionedValue<Optional<Wrapped<T>>> optionalWrapped,
    HanaUnwrapper unwrapper) {
    super(
      ImmutableList.of(optionalWrapped),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        if (!optionalWrapped.value().isPresent()) {
          return Optional.empty();
        }
        return Optional.of(optionalWrapped.value().get().unwrap(unwrapper));
      }
    );
  }
}
