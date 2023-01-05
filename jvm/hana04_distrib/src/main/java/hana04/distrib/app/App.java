package hana04.distrib.app;

import hana04.base.serialize.binary.BinarySerializer;
import hana04.distrib.request.Request;
import hana04.distrib.util.ParallelTasksUtil;
import hana04.distrib.util.RemoteServerUtil;
import hana04.distrib.worker.LocalWorker;
import hana04.distrib.worker.RemoteWorker;
import hana04.distrib.worker.WorkerPool;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Base class for an application that connects to a remote server in some way.
 */
public class App {
  private static Logger logger = LoggerFactory.getLogger(App.class);
  // User-specified data
  private int numLocalThreads;
  private int numRemoteThreads;
  private ArrayList<Pair<String, Integer>> remoteServers = new ArrayList<>();
  private String keyStoreFile;
  private boolean keyStoreLoaded = false;
  private String keyStorePassword;
  boolean useSecureConnection = false;
  // Dependencies
  private final BinarySerializer.Factory binarySerializerFactory;
  private final WorkerPool workerPool = new WorkerPool();
  private SocketFactory clientSocketFactory = null;

  App(BinarySerializer.Factory binarySerializerFactory) {
    this.binarySerializerFactory = binarySerializerFactory;
  }

  protected Options getOptions() {
    Options options = new Options();

    options.addOption("h", false, "Display this help text.");

    options.addOption(Option.builder("H").hasArg().argName("type-name").optionalArg(true).desc(
      "Display parameters of Request with the given type name. "
        + "If the type name is not valid, display those for all Request types.").build());

    options.addOption("q", false, "Quiet mode - do not print any log messages to stdout.");

    options.addOption(Option.builder("p").hasArg().argName("count").desc(
      "Override the detected number of processors to use for parallel rendering."
    ).build());

    options.addOption(Option.builder("c").hasArg().argName("hosts").desc(
      "Connect to slave renderers over a network.  Requires a semicolon-separated list " +
        "of the form host.domain[:port]."
    ).build());

    options.addOption(Option.builder("k").hasArg().argName("filename").desc(
      "Set the keystore file to use. (Default: data/keystore.jks)"
    ).build());

    options.addOption(Option.builder("w").hasArg().argName("password").desc(
      "Set the keystore password. (Default: seaweed)"
    ).build());

    options.addOption("z", false, "Use secure connection.");

    return options;
  }

  protected void processCommandLine(CommandLine cmd, Options options) {
    // Whether to show help.
    if (cmd.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java " + getClass().getName() + " [options] <xml-file>", options);
      System.exit(0);
    }

    // Quiet flag.
    if (cmd.hasOption('q')) {
      ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      root.setLevel(ch.qos.logback.classic.Level.OFF);
    }

    // Set the keystore file.
    if (cmd.hasOption("k")) {
      keyStoreFile = cmd.getOptionValue('k');
    } else {
      keyStoreFile = "data/keystore.jks";
    }

    // Set the keystore password
    if (cmd.hasOption('w')) {
      keyStorePassword = cmd.getOptionValue('w');
    } else {
      keyStorePassword = "seaweed";
    }

    // Use secure connection
    useSecureConnection = cmd.hasOption('z');

    // Find the number of local threads.
    if (cmd.hasOption('p')) {
      numLocalThreads = Integer.valueOf(cmd.getOptionValue('p'));
    } else {
      numLocalThreads = Runtime.getRuntime().availableProcessors();
    }
    createLocalWorkers();

    // Parse the servers.
    if (cmd.hasOption('c')) {
      String[] servers = cmd.getOptionValue('c').split(";");
      for (String serverString : servers) {
        if (serverString.indexOf(':') >= 0) {
          String[] comps = serverString.split(":");
          String name = comps[0];
          int port = Integer.valueOf(comps[1]);
          remoteServers.add(new ImmutablePair<>(name, port));
        } else {
          throw new RuntimeException("Invalid server name '" + serverString + "'.  It must contain a port.");
        }
      }
    }
    try {
      connectToRemoteServers();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private SocketFactory getSocketFactory(boolean secure) {
    if (secure) {
      return SSLSocketFactory.getDefault();
    } else {
      return SocketFactory.getDefault();
    }
  }

  void loadKeyStoreFile() {
    if (!keyStoreLoaded) {
      logger.info("Loading key store file '" + keyStoreFile + "'.");
      System.setProperty("javax.net.ssl.trustStore", keyStoreFile);
      System.setProperty("javax.net.ssl.trustStorePassword", keyStorePassword);
      keyStoreLoaded = true;
    }
  }

  void connectToRemoteServers() throws IOException {
    numRemoteThreads = 0;
    if (remoteServers.size() > 0) {
      if (useSecureConnection) {
        loadKeyStoreFile();
      }
      clientSocketFactory = getSocketFactory(useSecureConnection);

      for (Pair<String, Integer> pair : remoteServers) {
        String address = pair.getLeft();
        int port = pair.getRight();

        // Find out the thread count of each server.
        final int[] threadCount = new int[]{0};
        logger.info(String.format("Connecting to %s:%d ...", address, port));
        RemoteServerUtil.talkWithRemoteServer(clientSocketFactory, address, port,
          (DataInputStream input, DataOutputStream output) -> {
            try {
              output.writeUTF("getNumThreads");
              threadCount[0] = input.readInt();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
        logger.info(String.format("The server at %s:%d has %d threads.", address, port, threadCount[0]));

        createRemoteWorkers(threadCount[0], address, port);
      }
    }
  }

  private void createLocalWorkers() {
    // Populate the block renderer pool with local renderers.
    for (int i = 0; i < numLocalThreads; i++) {
      workerPool.deposit(new LocalWorker());
    }
  }

  private void createRemoteWorkers(int threadCount, String address, int port) {
    // Create the remote renderers.
    numRemoteThreads += threadCount;
    for (int j = 0; j < threadCount; j++) {
      workerPool.deposit(new RemoteWorker(clientSocketFactory, address, port));
    }
  }

  public class UploadRequest implements Callable<Integer> {
    public int index;
    public Request request;

    UploadRequest(Request request, int index) {
      this.index = index;
      this.request = request;
    }

    @Override
    public Integer call() {
      uploadRequestToRemoteServer(request, index);
      return 0;
    }
  }

  private void uploadRequestToRemoteServer(Request request, int serverIndex) {
    Pair<String, Integer> pair = remoteServers.get(serverIndex);
    String address = pair.getLeft();
    int port = pair.getRight();
    RemoteServerUtil.talkWithRemoteServer(clientSocketFactory, address, port,
      (DataInputStream input, DataOutputStream output) -> {
        try {
          logger.info(String.format("Uploading request to %s:%d ...", address, port));
          long start = System.currentTimeMillis();
          output.writeUTF("registerRequest");

          MessagePacker messagePacker = MessagePack.newDefaultPacker(output);
          BinarySerializer serializer = binarySerializerFactory.create(messagePacker);
          serializer.serialize(request);
          messagePacker.flush();

          String result = input.readUTF();
          if (!result.equals("success"))
            throw new RuntimeException(String.format("Request uploading to %s:%d failed.",
              address, port));
          long end = System.currentTimeMillis();
          long elapsedTime = end - start;
          logger.info(String.format("Successfully uploaded request to %s:%d.", address, port));
          logger.info(String.format("Elapsed time %d min(s) %d sec(s) %d ms.",
            elapsedTime / 60000, (elapsedTime / 1000) % 60, elapsedTime % 1000));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
  }

  void uploadRequestToRemoteServers(Request request) {
    ArrayList<Callable<Integer>> callables = new ArrayList<>();
    for (int i = 0; i < remoteServers.size(); i++) {
      callables.add(new UploadRequest(request, i));
    }
    ParallelTasksUtil.execute(
      Executors.newCachedThreadPool(),
      callables,
      60, TimeUnit.SECONDS);
  }

  void removeRequestFromRemoteServers(Request request) {
    for (Pair<String, Integer> pair : remoteServers) {
      String address = pair.getLeft();
      int port = pair.getRight();
      RemoteServerUtil.talkWithRemoteServer(clientSocketFactory, address, port,
        (DataInputStream input, DataOutputStream output) -> {
          try {
            logger.info("Requesting to remove request " + request.uuid().toString() + " from " + address
              + ":" + port + ".");
            output.writeUTF("removeRequest");
            output.writeUTF(request.uuid().toString());
            String result = input.readUTF();
            if (result.equals("success"))
              logger.info(String.format("Successfully removed request from %s:%d", address, port));
            else
              logger.info(String.format("Server %s:%d does not seem to have the request.", address, port));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    }
  }

  public void executeCommandOnAllRemoteServersInParallel(String command, UUID uuid,
                                                         boolean failHard, BiConsumer<DataInputStream,
    DataOutputStream> action) {

    ArrayList<Callable<Integer>> callables = new ArrayList<>();
    for (Pair<String, Integer> remoteServer : remoteServers) {
      final String address = remoteServer.getLeft();
      final int port = remoteServer.getRight();
      callables.add(() -> {
        try {
          RemoteServerUtil.talkWithRemoteServer(clientSocketFactory, address, port,
            (DataInputStream input, DataOutputStream output) -> {
              try {
                logger.info(String.format("Requesting %s:%d to perform command '%s' on request %s",
                  address, port, command, uuid.toString()));
                long start = System.currentTimeMillis();
                output.writeUTF(command);
                output.writeUTF(uuid.toString());

                action.accept(input, output);

                String result = input.readUTF();
                long end = System.currentTimeMillis();
                long elapsedTime = end - start;
                System.out.println("result = " + result);
                if (result.equals("success")) {
                  logger.info(String.format("Success (%s:%d) with command '%s'", address, port, command));
                  logger.info(String.format("Elapsed time %d min(s) %d sec(s) %d ms.",
                    elapsedTime / 60000, (elapsedTime / 1000) % 60, elapsedTime % 1000));
                } else {
                  String message = String.format("Failure (%s:%d) with command '%s'", address, port, command);
                  if (failHard) {
                    throw new RuntimeException(message);
                  } else {
                    logger.info(message);
                  }
                }
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
          return null;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
    ParallelTasksUtil.execute(Executors.newCachedThreadPool(), callables, 60, TimeUnit.SECONDS);
  }

  public int getNumLocalThreads() {
    return numLocalThreads;
  }

  public int getNumRemoteThreads() {
    return numRemoteThreads;
  }

  public WorkerPool getWorkerPool() {
    return workerPool;
  }

  public ArrayList<Pair<String, Integer>> getRemoteServers() {
    return remoteServers;
  }

  public SocketFactory getClientSocketFactory() {
    return clientSocketFactory;
  }

}
