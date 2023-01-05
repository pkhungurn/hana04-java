package hana04.distrib.request;

import hana04.base.changeprop.VersionedValue;
import hana04.base.extension.HanaObject;
import hana04.distrib.app.Main;
import hana04.distrib.app.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public interface Request extends HanaObject {
  UUID uuid();

  interface Runner {
    Request getRequest();

    void runMain(Main main);

    void runServer(Server server, String command, DataInputStream input, DataOutputStream output);

    String getDefaultOutputFileName(String inputFileName);

    interface Vv extends VersionedValue<Runner> {
      // NO-OP
    }
  }
}
