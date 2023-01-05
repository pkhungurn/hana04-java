package hana04.base.donothingobject;

import hana04.base.extension.donothing.DoNothingExtensionImpl;

import javax.inject.Inject;

public class DoNothingObject__DoNothingExtension__Factory implements DoNothingObject__ExtensionFactory {
  @Inject
  public DoNothingObject__DoNothingExtension__Factory() {
    // NO-OP
  }

  @Override
  public Object create(DoNothingObject extensible) {
    return new DoNothingExtensionImpl();
  }
}
