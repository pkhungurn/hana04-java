package tasuku.indexed;

import com.google.common.base.Preconditions;
import tasuku.TaskUtil;
import tasuku.Workspace;

import java.util.Collections;

public abstract class NoIndexFileTasks implements IndexedFileTasks {
  private final Workspace ws;
  private final String prefix;
  private final String commandName;

  public NoIndexFileTasks(Workspace ws, String prefix, String commandName) {
    this(ws, prefix, commandName, true);
  }

  public NoIndexFileTasks(Workspace ws, String prefix, String commandName, boolean createTasksImmediately) {
    this.ws = ws;
    this.prefix = prefix;
    this.commandName = commandName;
    if (createTasksImmediately) {
      createTasks();
    }
  }

  public abstract String getFileTaskName();

  public abstract void createFileTask();

  public void createTasks() {
    if (!ws.taskExists(getFileTaskName())) {
      createFileTask();
      ws.newCommandTask(getRunCommandName(), Collections.singletonList(getFileTaskName()), null);
      TaskUtil.createDeleteAllTask(ws, getCleanCommandName(),
          Collections.singletonList(getFileTaskName()));
    }
  }

  public String getFileTaskName(int... index) {
    Preconditions.checkArgument(
        index.length == 0,
        "Invalid arity: this indexed file task group has arity 0");
    return getFileTaskName();
  }

  public int[] getShape() {
    return new int[0];
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

  public int getArity() {
    return 0;
  }

  public String getCommandName() {
    return commandName;
  }
}
