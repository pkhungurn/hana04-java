package tasuku;

import org.slf4j.ILoggerFactory;
import tasuku.impl.FileTask;
import tasuku.impl.WorkspaceImpl;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Workspace {
  boolean taskExists(String taskName);

  boolean taskExistsAndNotPlaceholder(String taskName);

  Task newCommandTask(String taskName, List<String> dependencies, Runnable action);

  FileTask newFileTask(String taskName, List<String> dependencies, Runnable action);

  void newPlaceholderTask(String taskName);

  void startSession();

  void endSession();

  void run(String taskName);

  boolean needsToRun(String taskName);

  boolean canRun(String taskName);

  boolean isInSession();

  FileSystem getFileSystem();

  ILoggerFactory getLoggerFactory();

  Path getFilePath(String taskName);

  Map<String, String> getRoots();

  Set<String> getTaskNames();

  Task getTask(String name);

  interface Builder {
    Builder loggerFactory(ILoggerFactory loggerFactory);

    Builder fileSystem(FileSystem fileSystem);

    Workspace build();
  }

  static Builder builder() {
    return new WorkspaceImpl.Builder();
  }
}
