package tasuku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TaskUtil {
  public static final Logger logger = LoggerFactory.getLogger(TaskUtil.class.getName());

  public static final Runnable DO_NOTHING = () -> {
    // NO-OP
  };

  public static void createDeleteAllTask(Workspace ws, String taskName, List<String> filesToDelete) {
    ws.newCommandTask(taskName, Collections.emptyList(), () -> {
      for (String fileName : filesToDelete) {
        deleteFile(ws, fileName);
      }
    });
  }

  public static void deleteFile(Workspace workspace, String fileName) {
    Path path = workspace.getFileSystem().getPath(fileName);
    try {
      logger.info("Deleting " + fileName + " ...");
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
