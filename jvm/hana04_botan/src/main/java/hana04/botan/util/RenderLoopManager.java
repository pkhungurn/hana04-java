package hana04.botan.util;

import hana04.botan.cache.GlObjectCache;

public class RenderLoopManager {
  private static final double DEFAULT_RENDER_INTERVAL_SECONDS = 0.01;
  private static final double DEFAULT_GARBAGE_COLLECT_INTERVAL_SECONDS = 2.0;
  private final GlObjectCache glObjectCache;
  private final double renderIntervalSeconds;
  private final double gargateCollectIntervalSeconds;

  public RenderLoopManager(
    GlObjectCache glObjectCache,
    double renderIntervalSeconds,
    double garbageCollectIntervalSections) {
    this.glObjectCache = glObjectCache;
    this.renderIntervalSeconds = renderIntervalSeconds;
    this.gargateCollectIntervalSeconds = garbageCollectIntervalSections;
  }

  public RenderLoopManager(GlObjectCache glObjectCache) {
    this(glObjectCache, DEFAULT_RENDER_INTERVAL_SECONDS, DEFAULT_GARBAGE_COLLECT_INTERVAL_SECONDS);
  }

  public boolean maybeGarbageCollectAndCheckShouldRender() {
    double sinceLastRender = computeElapsedTimeSinceLastRender();
    if (sinceLastRender < renderIntervalSeconds) {
      return false;
    }

    double sinceLargeGarbageCollect = computeElapsedTimeSinceLastGarbageCollection();
    if (sinceLargeGarbageCollect > gargateCollectIntervalSeconds) {
      glObjectCache.collectGarbage();
    }

    return true;
  }

  private boolean renderTimingStarted = false;
  private long lastRenderTime;
  private boolean garbageCollectTimingStarted = false;
  private long lastGarbageCollectTime;

  protected double computeElapsedTimeSinceLastRender() {
    long currentTime = System.nanoTime();
    long elaspedTime = currentTime - lastRenderTime;
    if (!renderTimingStarted) {
      renderTimingStarted = true;
      lastRenderTime = currentTime;
    } else {
      if (elaspedTime * 1e-9 >= renderIntervalSeconds) {
        lastRenderTime = currentTime;
      }
    }
    return elaspedTime * 1e-9;
  }

  protected double computeElapsedTimeSinceLastGarbageCollection() {
    long currentTime = System.nanoTime();
    long elaspedTime = currentTime - lastGarbageCollectTime;
    if (!garbageCollectTimingStarted) {
      garbageCollectTimingStarted = true;
      lastGarbageCollectTime = currentTime;
    } else {
      if (elaspedTime * 1e-9 >= gargateCollectIntervalSeconds) {
        lastGarbageCollectTime = currentTime;
      }
    }
    return elaspedTime * 1e-9;
  }
}
