package tasuku.impl;

import tasuku.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractTask implements Task {
  final WorkspaceImpl workspace;
  private final String name;
  private final List<String> dependencies;

  AbstractTask(WorkspaceImpl workspace, String name, List<String> dependencies) {
    this.workspace = workspace;
    this.name = name;
    this.dependencies = dependencies;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<String> getDependencies() {
    return dependencies;
  }

  long getFileLastModified(String taskName) {
    Path path = workspace.getFilePath(taskName);
    if (!Files.exists(path)) {
      return Long.MAX_VALUE;
    } else {
      try {
        return Files.getLastModifiedTime(path).toMillis();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  boolean fileExists(String taskName) {
    return Files.exists(workspace.getFilePath(taskName));
  }
}
