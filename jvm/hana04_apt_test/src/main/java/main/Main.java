package main;

import hana04.base.donothingobject.DoNothingObject;
import hana04.base.donothingobject.DoNothingObjectBuilder;
import hana04.base.filesystem.FileSystemModule;

import java.nio.file.FileSystems;

public class Main {
  public static void main(String[] args) {
    Component component = DaggerComponent.builder()
      .fileSystemModule(new FileSystemModule(FileSystems.getDefault()))
      .build();

    DoNothingObjectBuilder nodeBuilder = component.uberFactory().create(DoNothingObjectBuilder.class);
    DoNothingObject node = nodeBuilder.build();
    System.out.println(node.getClass());
  }
}
