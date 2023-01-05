package hana04.distrib.app;

import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.distrib.request.Request;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server extends App {
  /**
   * The logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  /**
   * The port that this server listens to.
   */
  private int listeningPort;
  /**
   * The requests indexed by UUID string.
   */
  private final HashMap<String, Request> requests = new HashMap<>();
  /**
   * Wire deserializer factory.
   */
  private final BinaryDeserializer.Factory binaryDeserializerFactory;

  @Inject
  public Server(
    BinarySerializer.Factory binarySerializerFactory,
    BinaryDeserializer.Factory binaryDeserializerFactory) {
    super(binarySerializerFactory);
    this.binaryDeserializerFactory = binaryDeserializerFactory;
  }

  @Override
  protected Options getOptions() {
    Options options = super.getOptions();

    options.addOption(Option.builder("l").hasArg().argName("port").desc(
      "Listen for connections on a certain port (Default: 2822)."
    ).build());

    return options;
  }

  protected void processCommandLine(CommandLine cmd, Options options) {
    super.processCommandLine(cmd, options);

    // Set the listening port
    if (cmd.hasOption('l')) {
      listeningPort = Integer.valueOf(cmd.getOptionValue('l'));
    } else {
      listeningPort = 2828;
    }
  }

  public void run(String[] args) {
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    Options options = getOptions();
    try {
      cmd = parser.parse(options, args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    processCommandLine(cmd, options);

    try {
      connectToRemoteServers();

      ServerSocketFactory serverSocketFactory = null;
      if (useSecureConnection) {
        loadKeyStoreFile();
        serverSocketFactory = SSLServerSocketFactory.getDefault();
      } else {
        serverSocketFactory = ServerSocketFactory.getDefault();
      }

      int threadNumber = 0;
      ServerSocket serverSocket = serverSocketFactory.createServerSocket(listeningPort);
      logger.info("Listening on port " + listeningPort + ".");
      while (true) {
        try {
          Socket socket = serverSocket.accept();
          logger.info("Received a connection from " + socket.getInetAddress());
          threadNumber++;
          logger.info("Creating server thread #" + threadNumber + " ...");
          ServerThread serverThread = new ServerThread(socket, threadNumber);
          serverThread.start();
        } catch (Exception exception) {
          throw new RuntimeException(exception);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public class ServerThread extends Thread {
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final int threadNumber;

    ServerThread(Socket socket, int threadNumber) {
      this.socket = socket;
      try {
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.threadNumber = threadNumber;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private void info(String message) {
      logger.info(String.format("(Thread %d) %s", threadNumber, message));
    }

    public void run() {
      try {
        logger.info("Server thread #" + threadNumber + " has started.");
        serverLoop:
        while (true) {
          String command = input.readUTF();
          switch (command) {
            case "endSession":
              info("Remote client requested the session to end.");
              break serverLoop;
            case "getNumThreads":
              runGetNumThreads();
              break;
            case "registerRequest":
              runRegisterRequest();
              break;
            case "removeRequest":
              runRemoveRequest();
              break;
            default:
              delegateToRequest(command);
              break;
          }
        }
        logger.info("Server thread #" + threadNumber + " has ended.");
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          input.close();
          output.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    private void runGetNumThreads() throws IOException {
      info("Remote client requested the number of threads. [Answer = "
        + (getNumLocalThreads() + getNumRemoteThreads()) + "]");
      output.writeInt(getNumLocalThreads() + getNumRemoteThreads());
    }

    private void runRegisterRequest() throws IOException {
      info("Remote client requested to register a scene ... ");
      long start = System.currentTimeMillis();
      MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(input);
      Request request = binaryDeserializerFactory.create().deserialize(unpacker.unpackValue().asMapValue());
      System.out.println(request.getClass());
      request.prepareExtension(Request.Runner.Vv.class);
      long end = System.currentTimeMillis();
      long elaspedTime = end - start;
      info(String.format("Request reconstruction finished.  Elasped time = %d min(s), %d sec(s), %d ms",
        (elaspedTime / 60000), (elaspedTime / 1000) % 60, elaspedTime % 1000));
      requests.put(request.uuid().toString(), request);
      info(String.format("Registered request with UUID = %s.", request.uuid().toString()));
      uploadRequestToRemoteServers(request);
      output.writeUTF("success");
    }

    private void runRemoveRequest() throws IOException {
      String uuidString = input.readUTF();
      info(String.format("Remote client requested request with UUID = %s be removed.", uuidString));
      if (requests.containsKey(uuidString)) {
        Request request = requests.get(uuidString);
        removeRequestFromRemoteServers(request);
        requests.remove(uuidString);
        info("Successfully removed request with UUID = " + uuidString);
        output.writeUTF("success");
      } else {
        info("Scene with UUID = " + uuidString + " does not exist.");
        output.writeUTF("not found");
      }
    }

    private void delegateToRequest(String command) throws IOException {
      info("Remote client requested to process command '" + command + "' ...");
      long start = System.currentTimeMillis();

      String requestUUID = input.readUTF();
      if (!requests.containsKey(requestUUID)) {
        info("The request with UUID = " + requestUUID + " does not exist.");
        return;
      }
      Request request = requests.get(requestUUID);
      Request.Runner requestRunner = request.getExtension(Request.Runner.Vv.class).value();
      requestRunner.runServer(Server.this, command, input, output);

      long end = System.currentTimeMillis();
      long elapsedTime = end - start;
      info(String.format("Command '" + command + "' finished.  " +
          "Elapsed time = %d min(s), %d sec(s), %d ms",
        (elapsedTime / 60000), (elapsedTime / 1000) % 60, elapsedTime % 1000));
    }
  }
}
