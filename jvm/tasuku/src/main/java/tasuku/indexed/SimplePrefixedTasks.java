package tasuku.indexed;

import com.google.common.collect.ImmutableList;
import tasuku.TaskUtil;
import tasuku.Workspace;

import java.util.function.BiConsumer;

public class SimplePrefixedTasks implements PrefixedTasks {
  private final Workspace workspace;
  private final String prefix;
  private final String commandName;

  public SimplePrefixedTasks(
      Workspace workspace,
      String prefix,
      String commandName,
      BiConsumer<Workspace, String> createRunTask,
      BiConsumer<Workspace, String> createCleanTasks) {
    this.workspace = workspace;
    this.prefix = prefix;
    this.commandName = commandName;
    createRunTask.accept(workspace, getRunCommandName());
    createCleanTasks.accept(workspace, getCleanCommandName());
  }

  @Override
  public String getPrefix() {
    return prefix;
  }

  @Override
  public String getCommandName() {
    return commandName;
  }

  @Override
  public String getRunCommandName() {
    return prefix + "/" + commandName;
  }

  @Override
  public String getCleanCommandName() {
    return prefix + "/" + commandName + "_clean";
  }

  @Override
  public Workspace getWorkspace() {
    return workspace;
  }

  public static SimplePrefixedTasks fromFile(Workspace ws, String prefix, String commandName, String fileName) {
    return new SimplePrefixedTasks(
        ws,
        prefix,
        commandName,
        (workspace, name) -> workspace.newCommandTask(name, ImmutableList.of(fileName), () -> {
          // NO-OP
        }),
        (workspace, name) -> workspace.newCommandTask(
            name,
            ImmutableList.of(),
            () -> TaskUtil.deleteFile(workspace, fileName))
    );
  }
}
