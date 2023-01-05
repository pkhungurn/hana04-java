package hana04.base.caching;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.base.filesystem.FilePath;

import java.util.ArrayList;

public final class CacheKey {
  private static final String SEPARATOR = "|||||";

  public final String protocol;
  public final ImmutableList<CacheKeyPart> parts;
  public final String stringKey;

  private CacheKey(String protocol, ImmutableList<CacheKeyPart> parts, String stringKey) {
    this.protocol = protocol;
    this.parts = parts;
    this.stringKey = stringKey;
  }

  public static class Builder {
    private String protocol;
    private final ArrayList<CacheKeyPart> parts = new ArrayList<>();

    public Builder() {
      // NO-OP
    }

    public Builder protocol(String protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder addStringPart(String string) {
      this.parts.add(new StringCacheKeyPart(string));
      return this;
    }

    public Builder addFilePathPart(FilePath filePath) {
      this.parts.add(new FilePathCacheKeyPart(filePath));
      return this;
    }

    public CacheKey build() {
      Preconditions.checkNotNull(protocol, "Protocol is null");
      Preconditions.checkState(!protocol.isEmpty(), "Protocol is empty");
      Preconditions.checkState(!parts.isEmpty(), "There are no parts.");
      StringBuilder builder = new StringBuilder();
      builder.append(protocol);
      for (CacheKeyPart part : parts) {
        builder.append(SEPARATOR);
        builder.append(part.getStringPart());
      }
      return new CacheKey(protocol, ImmutableList.copyOf(parts), builder.toString());
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
