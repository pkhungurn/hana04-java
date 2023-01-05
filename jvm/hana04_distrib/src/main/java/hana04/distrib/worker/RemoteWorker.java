package hana04.distrib.worker;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;

public class RemoteWorker implements Worker {
  final private SocketFactory socketFactory;
  final private String address;
  final private int port;

  public RemoteWorker(SocketFactory socketFactory, String address, int port) {
    this.socketFactory = socketFactory;
    this.address = address;
    this.port = port;
  }

  public Socket createSocket() throws IOException {
    return socketFactory.createSocket(address, port);
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  public String getServerAndPort() {
    return String.format("%s:%d", address, port);
  }
}
