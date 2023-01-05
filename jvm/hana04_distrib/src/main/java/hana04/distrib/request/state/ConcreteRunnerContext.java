package hana04.distrib.request.state;

import hana04.distrib.app.App;
import hana04.distrib.app.Main;
import hana04.distrib.app.Server;
import hana04.distrib.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ConcreteRunnerContext implements RequestRunnerContext {
  private static final Logger logger = LoggerFactory.getLogger(ConcreteRunnerContext.class);
  private static final String CHANGE_STATE_COMMAND = "ConcreteRunnerContext.changeStateTo";

  protected RequestRunnerState currentState;

  protected final Request.Runner requestRunner;
  protected final Map<String, RequestRunnerState> nameToState;

  public ConcreteRunnerContext(Request.Runner requestRunner,
                               Map<String, RequestRunnerState> nameToState,
                               RequestRunnerState initialState) {
    this.requestRunner = requestRunner;
    this.nameToState = nameToState;
    this.currentState = initialState;
  }

  @Override
  public void changeStateTo(RequestRunnerState state, App app) {
    app.executeCommandOnAllRemoteServersInParallel(
      CHANGE_STATE_COMMAND,
      requestRunner.getRequest().uuid(),
      true,
      (input, output) -> {
        try {
          output.writeUTF(state.getName());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    currentState = state;
  }

  @Override
  public void runMain(Main main) {
    while (!currentState.isTerminating()) {
      logger.info("State = " + currentState.getName());
      currentState.runMain(this, main);
    }
  }

  @Override
  public void runServer(Server server, String command, DataInputStream input, DataOutputStream output) {
    logger.info("State = " + currentState.getName());
    if (command.equals(CHANGE_STATE_COMMAND)) {
      try {
        String stateName = input.readUTF();
        if (!nameToState.containsKey(stateName)) {
          output.writeUTF("failure");
        } else {
          changeStateTo(nameToState.get(stateName), server);
        }
        output.writeUTF("success");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      currentState.runServer(this, server, command, input, output);
    }
  }

  @Override
  public String getCurrentStateName() {
    return currentState.getName();
  }
}
