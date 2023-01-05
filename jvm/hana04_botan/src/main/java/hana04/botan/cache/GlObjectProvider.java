package hana04.botan.cache;

public interface GlObjectProvider<T> {
  void updateGlResource(GlObjectRecord record);
  T getGlObject();
}
