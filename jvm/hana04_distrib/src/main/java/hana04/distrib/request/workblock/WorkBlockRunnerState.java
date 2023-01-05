package hana04.distrib.request.workblock;

import hana04.distrib.app.App;
import hana04.distrib.app.Main;
import hana04.distrib.app.Server;
import hana04.distrib.request.state.RequestRunnerContext;
import hana04.distrib.request.state.RequestRunnerState;
import hana04.distrib.util.ObjectPoolMonitor;
import hana04.distrib.worker.LocalWorker;
import hana04.distrib.worker.RemoteWorker;
import hana04.distrib.worker.Worker;
import hana04.distrib.worker.WorkerPool;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import javax.net.SocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public abstract class WorkBlockRunnerState<T extends WorkBlock> implements RequestRunnerState {
  /**
   * Specified fields.
   */
  private final UUID requestUuid;
  /**
   * Computed fields.
   */
  protected ObjectPoolMonitor<T> requestedBlocks = new ObjectPoolMonitor<>();
  protected ObjectPoolMonitor<T> finishedBlocks = new ObjectPoolMonitor<>();
  private volatile int blocksLeft = 0;
  private CountDownLatch workerThreadCountDown;
  private CountDownLatch sendToServerThreadCountDown;
  private CountDownLatch receiveFromServerThreadCountDown;
  private HashMap<String, ObjectPoolMonitor<T>> toServersBlocks = new HashMap<>();
  private HashMap<String, HashMap<String, T>> waitingForServerBlocks = new HashMap<>();

  public WorkBlockRunnerState(UUID requestUuid) {
    this.requestUuid = requestUuid;
  }

  public String getPrepareCommand() {
    return getName() + ".prepare";
  }

  public String getEndCommand() {
    return getName() + ".end";
  }

  public String getSendBlocksCommand() {
    return getName() + ".sendBlock";
  }

  public String getReceiveBlocksCommand() {
    return getName() + "receiveBlock";
  }

  public abstract Logger logger();

  public abstract RequestRunnerState nextState();

  public abstract String getKey(T block);

  public abstract T createBlockFromKey(String key);

  public abstract void doWorkLocal(T block);

  public abstract int generateAndDepositBlocks();

  public abstract void saveBlock(T block);

  public void doWork(T block, Worker worker) {
    logger().info(String.format("Work on block with key=%s", getKey(block)));
    if (worker instanceof LocalWorker) {
      doWorkLocal(block);
    } else {
      doWorkRemote(block, (RemoteWorker) worker);
    }
    logger().info(String.format("Finished block with key=%s", getKey(block)));
  }

  public void doWorkRemote(T block, RemoteWorker worker) {
    HashMap<String, T> waitingBlocks = waitingForServerBlocks.get(worker.getServerAndPort());
    synchronized (waitingBlocks) {
      waitingBlocks.put(getKey(block), block);
    }
    toServersBlocks.get(worker.getServerAndPort()).deposit(block);
    try {
      synchronized (block) {
        block.wait();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void runMain(RequestRunnerContext context, Main main) {
    prepare(main);
    workMain();
    end(main);
    context.changeStateTo(nextState(), main);
  }

  private void prepare(App app) {
// Asks the servers to prepare for rendering.
    app.executeCommandOnAllRemoteServersInParallel(getPrepareCommand(), requestUuid,
            true,
            (DataInputStream input, DataOutputStream output) -> {
              // NO-OP
            });
    // Start the SendToServer threads.
    {
      int serverCount = app.getRemoteServers().size();
      sendToServerThreadCountDown = new CountDownLatch(serverCount);
      toServersBlocks.clear();
      for (int i = 0; i < serverCount; i++) {
        Pair<String, Integer> server = app.getRemoteServers().get(i);
        String serverString = String.format("%s:%d", server.getLeft(), server.getRight());
        toServersBlocks.put(serverString, new ObjectPoolMonitor<>());
        Thread thread = new Thread(new SendToServerThreads(app.getClientSocketFactory(),
                server.getLeft(), server.getRight(),
                toServersBlocks.get(serverString),
                sendToServerThreadCountDown));
        thread.setDaemon(true);
        thread.start();
      }
    }
    // Start the ReceiveFromServer threads.
    {
      int serverCount = app.getRemoteServers().size();
      receiveFromServerThreadCountDown = new CountDownLatch(serverCount);
      waitingForServerBlocks.clear();
      for (int i = 0; i < serverCount; i++) {
        Pair<String, Integer> server = app.getRemoteServers().get(i);
        String serverString = String.format("%s:%d", server.getLeft(), server.getRight());
        waitingForServerBlocks.put(serverString, new HashMap<>());
        Thread thread = new Thread(new ReceiveFromServerThread(app.getClientSocketFactory(),
                server.getLeft(), server.getRight(),
                waitingForServerBlocks.get(serverString),
                receiveFromServerThreadCountDown));
        thread.setDaemon(true);
        thread.start();
      }
    }
    // Start the worker threads.
    {
      int numTotalThreads = app.getNumLocalThreads() + app.getNumRemoteThreads();
      workerThreadCountDown = new CountDownLatch(numTotalThreads);
      for (int i = 0; i < numTotalThreads; i++) {
        Thread thread = new Thread(new WorkThread(app.getWorkerPool(), workerThreadCountDown));
        thread.setDaemon(true);
        thread.start();
      }
    }
  }

  private void workMain() {
    blocksLeft = generateAndDepositBlocks();
    logger().info(String.format("There are %d blocks.", blocksLeft));

    int numBlocks = blocksLeft;
    int blocksSofar = 0;
    while (blocksLeft > 0) {
      T block = finishedBlocks.fetch();
      saveBlock(block);
      blocksSofar++;
      String message = String.format("Finished %d blocks out of %d blocks (%03.2f%%)",
              blocksSofar, numBlocks, (blocksSofar) * 100.0 / numBlocks);
      logger().info(message);
      blocksLeft--;
    }
  }

  private void end(App app) {
    // Asks the servers to end rendering.
    app.executeCommandOnAllRemoteServersInParallel(getEndCommand(), requestUuid,
            true,
            (DataInputStream input, DataOutputStream output) -> {
              // NO-OP
            });
    // Put a null to the finishedBlocks so that the send thread terminates.
    finishedBlocks.deposit(null);
    // Stop the worker threads.
    {
      int numTotalThreads = app.getNumLocalThreads() + app.getNumRemoteThreads();
      // Deposit nulls so that threads can terminate themselves.
      for (int i = 0; i < numTotalThreads; i++) {
        requestedBlocks.deposit(null);
      }
      // Wait for the worker threads to terminate.
      try {
        workerThreadCountDown.await();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      logger().info("Terminated all worker threads!");
    }
    // Stop the SendToServerThreads
    {
      int serverCount = app.getRemoteServers().size();
      for (int i = 0; i < serverCount; i++) {
        Pair<String, Integer> server = app.getRemoteServers().get(i);
        String serverString = String.format("%s:%d", server.getLeft(), server.getRight());
        toServersBlocks.get(serverString).deposit(null);
      }
      try {
        sendToServerThreadCountDown.await();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      logger().info("Terminated all sendToServer threads!");
    }
    // Wait for the ReceiveFromServerThreads to terminate.
    {
      try {
        receiveFromServerThreadCountDown.await();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      logger().info("Terminated all receiveFromServer threads!");
    }
  }

  private void writeMessage(DataOutputStream output, String message) {
    try {
      output.writeUTF(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void receiveBlocksServer(Server server, DataInputStream input, DataOutputStream output) {
    try {
      while (true) {
        String command_ = input.readUTF();
        if (command_.equals("request")) {
          String key = input.readUTF();
          T block = createBlockFromKey(key);
          requestedBlocks.deposit(block);
          output.writeUTF("received");
        } else if (command_.equals("done")) {
          break;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    writeMessage(output, "success");
  }

  public void sendBlocksServer(Server server, DataInputStream input, DataOutputStream output) {
    try {
      while (true) {
        T block = finishedBlocks.fetch();
        if (block == null) {
          output.writeUTF("done");
          break;
        } else {
          output.writeUTF("result");
          output.writeUTF(getKey(block));
          block.serializeContent(output);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void prepareServer(Server server, DataInputStream input, DataOutputStream output) {
    prepare(server);
    writeMessage(output, "success");
  }

  public void endServer(Server server, DataInputStream input, DataOutputStream output) {
    end(server);
    writeMessage(output, "success");
  }

  public void runServer(RequestRunnerContext context,
                        Server server,
                        String command,
                        DataInputStream input,
                        DataOutputStream output) {
    if (command.equals(getPrepareCommand())) {
      prepareServer(server, input, output);
    } else if (command.equals(getEndCommand())) {
      endServer(server, input, output);
    } else if (command.equals(getSendBlocksCommand())) {
      sendBlocksServer(server, input, output);
    } else if (command.equals(getReceiveBlocksCommand())) {
      receiveBlocksServer(server, input, output);
    }
  }

  public boolean isTerminating() {
    return false;
  }

  class WorkThread implements Runnable {
    final CountDownLatch latch;
    final WorkerPool pool;

    public WorkThread(WorkerPool pool, CountDownLatch latch) {
      this.latch = latch;
      this.pool = pool;
    }

    @Override
    public void run() {
      while (true) {
        T block = requestedBlocks.fetch();
        if (block == null) {
          break;
        } else {
          Worker worker = pool.fetch();
          doWork(block, worker);
          pool.deposit(worker);
          finishedBlocks.deposit(block);
        }
      }
      latch.countDown();
    }
  }

  class SendToServerThreads implements Runnable {
    final CountDownLatch latch;
    final String address;
    final int port;
    final Socket socket;
    final DataInputStream input;
    final DataOutputStream output;
    final ObjectPoolMonitor<T> blockPool;

    public SendToServerThreads(SocketFactory socketFactory,
                               String address, int port,
                               ObjectPoolMonitor<T> blockPool,
                               CountDownLatch latch) {
      this.latch = latch;
      this.blockPool = blockPool;
      this.address = address;
      this.port = port;
      try {
        socket = socketFactory.createSocket(address, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        output.writeUTF(getReceiveBlocksCommand());
        output.writeUTF(requestUuid.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void run() {
      try {
        while (true) {
          T block = blockPool.fetch();
          if (block == null) {
            output.writeUTF("done");
            String response = input.readUTF();
            if (!response.equals("success")) {
              throw new RuntimeException(String.format(
                      "Could not shutdown the listening thread at " +
                              "%s:%d!", address, port));
            } else {
              break;
            }
          } else {
            logger().info("Requesting the server at " + address + ":" + port +
                    " to work on block with key=" + getKey(block));
            output.writeUTF("request");
            output.writeUTF(getKey(block));
            String response = input.readUTF();
            if (!response.equals("received")) {
              throw new RuntimeException(String.format(
                      "The request that the server at %s:%d to render block with key %s was not received!",
                      address, port, getKey(block)));
            }
          }
        }
        output.writeUTF("endSession");
        input.close();
        output.close();
        socket.close();
        latch.countDown();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  class ReceiveFromServerThread implements Runnable {
    final CountDownLatch latch;
    final String address;
    final int port;
    final Socket socket;
    final DataInputStream input;
    final DataOutputStream output;
    final HashMap<String, T> waitingBlocks;

    public ReceiveFromServerThread(SocketFactory socketFactory,
                                   String address, int port,
                                   HashMap<String, T> waitingBlocks,
                                   CountDownLatch latch) {
      this.address = address;
      this.port = port;
      this.latch = latch;
      this.waitingBlocks = waitingBlocks;
      try {
        socket = socketFactory.createSocket(address, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        output.writeUTF(getSendBlocksCommand());
        output.writeUTF(requestUuid.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void run() {
      try {
        while (true) {
          String command = input.readUTF();
          if (command.equals("done")) {
            break;
          } else if (command.equals("result")) {
            String key = input.readUTF();
            T block;
            synchronized (waitingBlocks) {
              if (!waitingBlocks.containsKey(key)) {
                block = null;
              } else {
                block = waitingBlocks.get(key);
                waitingBlocks.remove(key);
              }
            }
            if (block == null)
              throw new RuntimeException("The block with key '" + key + "' is not in the waiting queue!");
            else {
              block.deserializeContent(input);
            }
            synchronized (block) {
              block.notify();
            }
          } else {
            throw new RuntimeException("Invalid command on the ReceiveFromServerThread: " + command);
          }
        }
        output.writeUTF("endSession");
        input.close();
        output.close();
        socket.close();
        latch.countDown();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
