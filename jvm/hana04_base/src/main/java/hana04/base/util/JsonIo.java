package hana04.base.util;

import com.cedarsoftware.util.io.JsonWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.nio.file.FileSystem;

public class JsonIo {
  private final FileSystem fileSystem;

  @Inject
  public JsonIo(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Object load(String fileName) {
    String content = TextIo.readTextFile(fileSystem.getPath(fileName));
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(content, Object.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void write(Object json, String fileName, boolean prettyPrint) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      if (prettyPrint) {
        TextIo.writeTextFile(
            fileSystem.getPath(fileName),
            JsonWriter.formatJson(mapper.writeValueAsString(json)));
      } else {
        TextIo.writeTextFile(fileSystem.getPath(fileName), mapper.writeValueAsString(json));
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
