package hana04.botan.glasset.program;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.provider.GlProgramProvider;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlWrapper;

public class ProgramAssets {
  public static class GlProvider implements GlProgramProvider {
    private final GlObjectCache glObjectCache;
    private final ProgramSources sources;

    @HanaDeclareExtension(
      extensibleClass = ProgramAsset.class,
      extensionClass = GlProgramProvider.class)
    public GlProvider(ProgramAsset programData, GlObjectCache glObjectCache) {
      this.glObjectCache = glObjectCache;
      this.sources = programData.getExtension(ProgramSources.class);
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      boolean needUpdate = record.resource == null || record.version != sources.version();
      if (needUpdate) {
        sources.update();
        record.sizeInBytes = 0;
        GlWrapper glWrapper = glObjectCache.getGlWrapper();
        record.resource = glWrapper.createProgram(
          sources.getVertexShaderSource(),
          sources.getVertexShaderFileName(),
          sources.getFragmentShaderSource(),
          sources.getFragmentShaderFileName());
        record.version = sources.version();
      }
    }

    @Override
    public GlProgram getGlObject() {
      return (GlProgram) glObjectCache.getGLResource(this);
    }
  }
}
