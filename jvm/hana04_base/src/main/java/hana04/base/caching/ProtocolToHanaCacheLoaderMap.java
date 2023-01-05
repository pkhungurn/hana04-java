package hana04.base.caching;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ProtocolToHanaCacheLoaderMap {
  private final Map<String, HanaCacheLoader<?>> protocolToCacheLoader;

  @Inject
  public ProtocolToHanaCacheLoaderMap(Map<String, HanaCacheLoader<?>> protocolToCacheLoader) {
    this.protocolToCacheLoader = protocolToCacheLoader;
  }

  public HanaCacheLoader<?> get(String protocol) {
    return Preconditions.checkNotNull(protocolToCacheLoader.get(protocol), protocol);
  }
}
