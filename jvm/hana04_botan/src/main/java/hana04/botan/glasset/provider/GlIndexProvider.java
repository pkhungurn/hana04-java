package hana04.botan.glasset.provider;

import hana04.botan.cache.GlObjectProvider;
import hana04.opengl.wrapper.GlVbo;

public interface GlIndexProvider extends GlObjectProvider<GlVbo> {
  int getIndexCount();
}
