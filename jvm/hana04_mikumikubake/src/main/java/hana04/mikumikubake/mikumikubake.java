package hana04.mikumikubake;

import hana04.mikumikubake.mmd.MmdModelManager;

import javax.inject.Inject;
import javax.inject.Provider;

public class mikumikubake {
  private final Provider<MmdModelManager> mmdModelManagerProvider;

  @Inject
  mikumikubake(Provider<MmdModelManager> mmdModelManagerProvider) {
    this.mmdModelManagerProvider = mmdModelManagerProvider;
  }

  public MmdModelManager mmdModelManager() {
    return mmdModelManagerProvider.get();
  }
}
