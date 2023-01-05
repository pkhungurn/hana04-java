package tasuku.indexed;

import tasuku.Workspace;

public interface PrefixedTasks {
  String getPrefix();
  String getCommandName();
  String getRunCommandName();
  String getCleanCommandName();
  Workspace getWorkspace();
}
