package tasuku.indexed;

import com.google.common.collect.Sets;
import tasuku.Workspace;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class HigherLevelPrefixedTasks implements PrefixedTasks {
  private final Workspace workspace;
  private final String prefix;
  private final String commandName;

  public HigherLevelPrefixedTasks(
      Workspace workspace,
      String prefix,
      String commandName,
      Collection<PrefixedTasks> dependencies) {
    this.workspace = workspace;
    this.prefix = prefix;
    this.commandName = commandName;
    workspace.newCommandTask(
        getRunCommandName(),
        dependencies.stream().map(PrefixedTasks::getRunCommandName).collect(toImmutableList()),
        () -> {
          // NO-OP
        });
    workspace.newCommandTask(
        getCleanCommandName(),
        dependencies.stream().map(PrefixedTasks::getCleanCommandName).collect(toImmutableList()),
        () -> {
          // NO-OP
        });
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

  public static Map<String, PrefixedTasks> create(Workspace workspace,
      String prefix,
      Collection<Map<String, PrefixedTasks>> tasksList) {
    Set<String> taskNames = new HashSet<>();
    for (var tasks : tasksList) {
      taskNames = Sets.union(tasks.keySet(), taskNames);
    }
    HashMap<String, PrefixedTasks> output = new HashMap<>();
    for (var taskName : taskNames) {
      var dependencies = tasksList
          .stream()
          .filter(tasks -> tasks.containsKey(taskName))
          .map(tasks -> tasks.get(taskName))
          .collect(toImmutableList());
      output.put(taskName, new HigherLevelPrefixedTasks(workspace, prefix, taskName, dependencies));
    }
    return output;
  }
}
