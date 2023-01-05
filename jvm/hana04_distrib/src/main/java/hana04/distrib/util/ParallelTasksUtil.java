package hana04.distrib.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelTasksUtil {
  public static void consumeResultsFromCompletionService(CompletionService completionService, int count) {
    try {
      for (int i = 0; i < count; i++) {
        Future future = completionService.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> void execute(ExecutorService executorService,
                                 List<Callable<T>> callables,
                                 long timeOutNumber, TimeUnit timeOutUnit) {
    CompletionService completionService = new ExecutorCompletionService<T>(executorService);
    for (Callable callable : callables) {
      completionService.submit(callable);
    }
    consumeResultsFromCompletionService(completionService, callables.size());
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(timeOutNumber, timeOutUnit)) {
        executorService.shutdownNow();
      }
    } catch (Exception e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
