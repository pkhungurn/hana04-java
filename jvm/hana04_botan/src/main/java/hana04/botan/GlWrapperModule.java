package hana04.botan;

import dagger.Module;
import dagger.Provides;
import hana04.opengl.wrapper.GlWrapper;

@Module
public class GlWrapperModule {
  private final GlWrapper glWrapper;

  public GlWrapperModule(GlWrapper glWrapper) {
    this.glWrapper = glWrapper;
  }

  @Provides
  public GlWrapper providesGlWrapper() {
    return glWrapper;
  }
}
