package hana04.base.changeprop;


import com.google.common.base.Preconditions;

public class VersionManager {
  private long version = 0;

  public synchronized long getVersion() {
    return version;
  }

  public synchronized long bumpVersion() {
    version += 1;
    return version;
  }

  public synchronized void setVersion(long newValue) {
    Preconditions.checkArgument(newValue > version);
    version = newValue;
  }
}
