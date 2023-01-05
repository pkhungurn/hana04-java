package tasuku.indexed;

import com.google.common.base.Preconditions;
import tasuku.TaskUtil;
import tasuku.Workspace;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class OneIndexFileTasks implements IndexedFileTasks {
  private final Workspace ws;
  private final String prefix;
  private final String shortCommandName;
  private final int fileTaskCount;
  private List<String> fileTaskNames;

  public OneIndexFileTasks(Workspace ws,
      String prefix,
      String commandName,
      int count,
      boolean createTasksImmediately) {
    this.ws = ws;
    this.prefix = prefix;
    this.shortCommandName = commandName;
    this.fileTaskCount = count;
    if (createTasksImmediately) {
      createTasks();
    }
  }

  public abstract String getFileTaskName(int index);

  public abstract void createFileTask(int index);

  @Override
  public String getRunCommandName() {
    return prefix + "/" + shortCommandName;
  }

  @Override
  public String getCleanCommandName() {
    return prefix + "/" + shortCommandName + "_clean";
  }

  protected void createTasks() {
    fileTaskNames = IntStream.range(0, fileTaskCount).mapToObj(this::getFileTaskName).collect(Collectors.toList());
    if (fileTaskCount == 0) {
      return;
    }
    if (ws.taskExists(fileTaskNames.get(0))) {
      return;
    }
    IntStream.range(0, fileTaskCount).forEach(this::createFileTask);
    ws.newCommandTask(getRunCommandName(), fileTaskNames, null);
    TaskUtil.createDeleteAllTask(ws, getCleanCommandName(), fileTaskNames);
  }

  @Override
  public String getPrefix() {
    return prefix;
  }

  @Override
  public int[] getShape() {
    return new int[]{fileTaskCount};
  }

  @Override
  public Workspace getWorkspace() {
    return ws;
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public String getFileTaskName(int... index) {
    Preconditions.checkArgument(
        index.length == 1,
        "Invalid arity: this file task group has arity 1");
    return getFileTaskName(index[0]);
  }

  @Override
  public String getCommandName() {
    return shortCommandName;
  }
}
