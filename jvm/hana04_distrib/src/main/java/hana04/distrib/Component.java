package hana04.distrib;

import hana04.distrib.app.Main;
import hana04.distrib.app.Server;

public interface Component {
  Main distribMain();
  Server distribServer();
}
