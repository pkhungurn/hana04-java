package hana04.distrib.request.workblock;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface WorkBlock {
  void serializeContent(DataOutputStream stream);
  void deserializeContent(DataInputStream stream);
}
