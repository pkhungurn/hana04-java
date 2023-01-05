package tasuku.impl;

import com.google.common.base.Preconditions;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import tasuku.Task;
import tasuku.Workspace;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkspaceImpl implements Workspace {
  private final HashMap<String, String> rootPaths = new HashMap<>();
  private final HashMap<String, Task> tasks = new HashMap<>();
  private final ILoggerFactory loggerFactory;
  private final FileSystem fileSystem;
  private HashMap<String, Boolean> resolvedNameToConsideredDone;
  private State state = State.OUT_OF_SESSION;
  private boolean modified;

  private enum State {
    OUT_OF_SESSION,
    IN_SESSION
  }

  WorkspaceImpl(FileSystem fileSystem, ILoggerFactory loggerFactory) {
    this.loggerFactory = loggerFactory;
    this.fileSystem = fileSystem;
    this.modified = false;
  }

  @Override
  public boolean taskExists(String taskName) {
    return tasks.containsKey(taskName);
  }

  @Override
  public boolean taskExistsAndNotPlaceholder(String taskName) {
    return taskExists(taskName) && !(getTask(taskName) instanceof PlaceholderTask);
  }

  @Override
  public Task newCommandTask(String taskName, List<String> dependencies, Runnable action) {
    Preconditions.checkState(
        !isInSession(),
        "New tasks must only be create when the workspace is out of session.");
    Preconditions.checkArgument(
        !taskExistsAndNotPlaceholder(taskName),
        "Task " + taskName + " already exists!");
    dependencies.forEach(this::newPlaceholderTask);
    Task task = new CommandTask(this, taskName, dependencies, action);
    tasks.put(taskName, task);
    modified = true;
    return task;
  }

  @Override
  public FileTask newFileTask(String taskName, List<String> dependencies, Runnable action) {
    Preconditions.checkState(
        !isInSession(),
        "New tasks must only be create when the workspace is out of session.");
    Preconditions.checkArgument(
        !taskExistsAndNotPlaceholder(taskName),
        "Task " + taskName + " already exists!");
    dependencies.forEach(this::newPlaceholderTask);
    FileTask task = new FileTask(this, taskName, dependencies, action, loggerFactory);
    tasks.put(taskName, task);
    modified = true;
    return task;
  }

  @Override
  public void newPlaceholderTask(String taskName) {
    Preconditions.checkState(
        !isInSession(),
        "New tasks must only be create when the workspace is out of session.");
    if (!taskExists(taskName)) {
      PlaceholderTask task = new PlaceholderTask(this, taskName);
      tasks.put(taskName, task);
      modified = true;
    }
  }

  @Override
  public void startSession() {
    Preconditions.checkState(
        !isInSession(),
        "A session can only be started when the workspace is out of session.");
    if (modified) {
      checkCycle();
    }
    state = State.IN_SESSION;
    resolvedNameToConsideredDone = new HashMap<>();
    modified = false;
  }

  private void checkCycle() {
    HashMap<String, NodeState> nodeState = new HashMap<>();
    for (Map.Entry<String, Task> entry : tasks.entrySet()) {
      String node = entry.getKey();
      if (!nodeState.containsKey(node)) {
        dfs(node, nodeState);
      }
    }
  }

  private enum NodeState {
    IN_STACK,
    VISITED
  }

  private void dfs(String node, HashMap<String, NodeState> nodeState) {
    nodeState.put(node, NodeState.IN_STACK);
    Task task = tasks.get(node);
    for (String dep : task.getDependencies()) {
      if (!nodeState.containsKey(dep)) {
        dfs(dep, nodeState);
      } else {
        NodeState state = nodeState.get(dep);
        if (state == NodeState.IN_STACK) {
          throw new IllegalStateException("WorkspaceImpl.checkCycle(): Discovered cyclic dependency!");
        }
      }
    }
    nodeState.put(node, NodeState.VISITED);
  }

  @Override
  public void endSession() {
    Preconditions.checkState(
        isInSession(),
        "A session can only be ended when the workspace is in session.");
    state = State.OUT_OF_SESSION;
    resolvedNameToConsideredDone = null;
  }

  @Override
  public boolean isInSession() {
    return state.equals(State.IN_SESSION);
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public Path getFilePath(String taskName) {
    return fileSystem.getPath(taskName);
  }

  @Override
  public Map<String, String> getRoots() {
    return new HashMap<>(rootPaths);
  }

  @Override
  public Set<String> getTaskNames() {
    return tasks.keySet();
  }

  @Override
  public void run(String taskName) {
    Preconditions.checkState(
        isInSession(),
        "A task can only be run when the workspace is in session.");
    if (!taskExists(taskName)) {
      throw new IllegalArgumentException("Task " + taskName + " does not exists");
    }
    runHelper(taskName);
  }

  private void runHelper(String resolvedTaskName) {
    Task task = getTask(resolvedTaskName);
    for (String depName : task.getDependencies()) {
      if (needsToRun(depName)) {
        runHelper(depName);
      }
    }
    if (needsToRun(resolvedTaskName)) {
      task.run();
      resolvedNameToConsideredDone.put(resolvedTaskName, true);
    }
  }

  @Override
  public boolean needsToRun(String taskName) {
    Preconditions.checkState(
        isInSession(),
        "You can only check whether a task needs to run when the workspace is in session.");
    if (resolvedNameToConsideredDone.containsKey(taskName)) {
      return !resolvedNameToConsideredDone.get(taskName);
    } else {
      Task task = getTask(taskName);
      boolean needToRunValue = task.needsToBeRun();
      resolvedNameToConsideredDone.put(taskName, !needToRunValue);
      return needToRunValue;
    }
  }

  @Override
  public Task getTask(String taskName) {
    Preconditions.checkArgument(
        taskExists(taskName),
        "Task " + taskName + " does not exists!");
    return tasks.get(taskName);
  }

  @Override
  public boolean canRun(String taskName) {
    return getTask(taskName).canRun();
  }

  public static class Builder implements Workspace.Builder {
    private final HashMap<String, String> rootPaths = new HashMap<>();
    private ILoggerFactory loggerFactory;
    private FileSystem fileSystem;

    public Builder() {
      loggerFactory = LoggerFactory.getILoggerFactory();
      fileSystem = FileSystems.getDefault();
    }

    @Override
    public Builder loggerFactory(ILoggerFactory loggerFactory) {
      this.loggerFactory = loggerFactory;
      return this;
    }

    @Override
    public Builder fileSystem(FileSystem fileSystem) {
      this.fileSystem = fileSystem;
      return this;
    }

    @Override
    public WorkspaceImpl build() {
      WorkspaceImpl result = new WorkspaceImpl(fileSystem, loggerFactory);
      for (Map.Entry<String, String> entry : rootPaths.entrySet()) {
        result.rootPaths.put(entry.getKey(), entry.getValue());
      }
      return result;
    }
  }
}
