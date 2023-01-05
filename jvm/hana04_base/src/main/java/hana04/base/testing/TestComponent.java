package hana04.base.testing;

import hana04.base.Component;
import hana04.base.Module;
import hana04.base.filesystem.FileSystemModule;
import hana04.base.serialize.binary.BinaryDeserializer;

import javax.inject.Singleton;
import java.nio.file.FileSystems;

@Singleton
@dagger.Component(
  modules = {
    Module.class
  }
)
public interface TestComponent extends Component {
  static void main(String[] args) {
    TestComponent component = DaggerTestComponent.builder()
      .fileSystemModule(new FileSystemModule(FileSystems.getDefault()))
      .build();
    BinaryDeserializer binaryDeserializer = component.binaryDeserializerFactory().create();
  }
}
