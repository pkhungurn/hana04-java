package tasuku.indexed;

import com.google.common.base.Preconditions;
import tasuku.Workspace;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllTasks implements PrefixedTasks {
  private final Workspace ws;
  private final String prefix;
  private final String commandName;
  private final Collection<? extends PrefixedTasks> tasks;

  public AllTasks(Workspace ws, String prefix, String commandName, Collection<? extends PrefixedTasks> tasks) {
    this.ws = ws;
    this.prefix = prefix;
    this.commandName = commandName;
    this.tasks = tasks;
    createTasks();
  }

  public void createTasks() {
    if (!ws.taskExists(getCommandTaskName())) {
      ws.newCommandTask(
          getRunCommandName(),
          tasks.stream().map(PrefixedTasks::getRunCommandName).collect(Collectors.toList()),
          () -> {
            // NO-OP
          });
      ws.newCommandTask(
          getCleanCommandName(),
          tasks.stream().map(PrefixedTasks::getCleanCommandName).collect(Collectors.toList()),
          () -> {
            // NO-OP
          });
    }
  }

  public String getCommandTaskName(int... index) {
    Preconditions.checkArgument(
        index.length == 0,
        "Invalid arity: this indexed file task group has arity 0");
    return getRunCommandName();
  }

  public String getRunCommandName() {
    return prefix + "/" + commandName;
  }

  public String getCleanCommandName() {
    return prefix + "/" + commandName + "_clean";
  }

  public Workspace getWorkspace() {
    return ws;
  }

  public String getPrefix() {
    return prefix;
  }

  @Override
  public String getCommandName() {
    return commandName;
  }
}
