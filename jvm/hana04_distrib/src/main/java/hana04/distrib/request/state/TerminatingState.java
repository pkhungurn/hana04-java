package hana04.distrib.request.state;

import hana04.distrib.app.Main;
import hana04.distrib.app.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TerminatingState implements RequestRunnerState {
  private final static TerminatingState INSTANCE = new TerminatingState();

  private TerminatingState() {
    // NO-OP
  }

  public static TerminatingState v() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return TerminatingState.class.getName();
  }

  @Override
  public void runMain(RequestRunnerContext context, Main main) {
    // NO-OP
  }

  @Override
  public void runServer(RequestRunnerContext context, Server server, String command, DataInputStream input, DataOutputStream output) {
    // NO-OP
  }

  @Override
  public boolean isTerminating() {
    return true;
  }
}
