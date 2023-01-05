package hana04.distrib.request.state;

import hana04.distrib.app.App;
import hana04.distrib.app.Main;
import hana04.distrib.app.Server;
import hana04.distrib.request.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Implements the state design pattern's context part for the {@link Request.Runner}
 * object.
 */
public interface RequestRunnerContext {
  void changeStateTo(RequestRunnerState state, App app);

  void runMain(Main main);

  void runServer(Server server, String command, DataInputStream input, DataOutputStream output);

  String getCurrentStateName();
}
