package hana04.distrib.request.state;

import hana04.distrib.app.Main;
import hana04.distrib.app.Server;
import hana04.distrib.request.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Implements the state design pattern's state part for the {@link Request.Runner}
 * object.
 */
public interface RequestRunnerState {
  String getName();

  void runMain(RequestRunnerContext context, Main main);

  void runServer(RequestRunnerContext context,
                 Server server,
                 String command,
                 DataInputStream input,
                 DataOutputStream output);

  boolean isTerminating();
}
