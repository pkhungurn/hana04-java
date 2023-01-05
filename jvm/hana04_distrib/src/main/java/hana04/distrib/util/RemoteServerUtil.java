package hana04.distrib.util;

import javax.net.SocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;

public class RemoteServerUtil {
  public static void talkWithRemoteServer(SocketFactory socketFactory, String serverName, int port,
                                          BiConsumer<DataInputStream, DataOutputStream> action) {
    try {
      Socket socket = socketFactory.createSocket(serverName, port);
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      action.accept(input, output);
      output.writeUTF("endSession");
      input.close();
      output.close();
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
