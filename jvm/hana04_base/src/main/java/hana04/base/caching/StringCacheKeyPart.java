package hana04.base.caching;

public class StringCacheKeyPart implements CacheKeyPart {
  public final String value;

  public StringCacheKeyPart(String value) {
    this.value = value;
  }

  @Override
  public String getStringPart() {
    return "String:::" + value;
  }
}
