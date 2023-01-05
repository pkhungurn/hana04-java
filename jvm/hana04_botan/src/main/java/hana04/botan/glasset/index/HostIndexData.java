package hana04.botan.glasset.index;

import hana04.base.changeprop.VersionedSubject;
import hana04.botan.glasset.provider.HostBufferProvider;

public interface HostIndexData extends HostBufferProvider, VersionedSubject {
  int getIndexCount();
}
