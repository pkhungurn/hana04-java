package hana04.distrib.util;

import hana04.base.util.TextIo;

import java.nio.file.Path;

public class TimingInfoWriter {
  public static void write(long elapsedMillis, String[] arguments, Path path) {
    StringBuilder builder = new StringBuilder();
    long minute = elapsedMillis / (60 * 1000);
    long second = (elapsedMillis / 1000) % 60;
    long ms = elapsedMillis % 1000;
    builder.append(String.format("%d\n%d\n%d\n", minute, second, ms));
    for (String argument : arguments) {
      builder.append(argument);
      builder.append(" ");
    }
    builder.append("\n");
    TextIo.writeTextFile(path, /* content= */ builder.toString());
  }
}
