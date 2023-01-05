package hana04.mikumikubake.opengl.common;

import dagger.Provides;

@dagger.Module
public class GlWindowIdModule {

  private final long glWindowId;

  public GlWindowIdModule(long glWindowId) {
    this.glWindowId = glWindowId;
  }

  @Provides
  @GlWindowId
  public long glWindowId() {
    return glWindowId;
  }
}
