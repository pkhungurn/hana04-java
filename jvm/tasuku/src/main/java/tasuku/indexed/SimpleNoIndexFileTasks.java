package tasuku.indexed;

import tasuku.Workspace;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SimpleNoIndexFileTasks extends NoIndexFileTasks {
  private final Supplier<String> fileTaskNameSupplier;
  private final BiConsumer<Workspace, String> createFileTaskFunc;

  public SimpleNoIndexFileTasks(
      Workspace ws,
      String prefix,
      String shortCommandName,
      Supplier<String> fileTaskNameSupplier,
      BiConsumer<Workspace, String> createFileTaskFunc) {
    super(ws, prefix, shortCommandName, false);
    this.fileTaskNameSupplier = fileTaskNameSupplier;
    this.createFileTaskFunc = createFileTaskFunc;
    createTasks();
  }

  @Override
  public String getFileTaskName() {
    return fileTaskNameSupplier.get();
  }

  @Override
  public void createFileTask() {
    createFileTaskFunc.accept(getWorkspace(), getFileTaskName());
  }
}
