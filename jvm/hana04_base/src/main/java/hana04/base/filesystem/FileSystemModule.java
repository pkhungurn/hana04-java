package hana04.base.filesystem;

import dagger.Module;
import dagger.Provides;

import java.nio.file.FileSystem;

@Module
public class FileSystemModule {
  private final FileSystem fileSystem;

  public FileSystemModule(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Provides
  public FileSystem providesFileSystem() {
    return fileSystem;
  }
}
