package main.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import hana04.base.filesystem.FileSystemModule;
import main.Component;
import main.DaggerComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.FileSystem;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TestMapTest {
  private Component component;

  @Before
  public void setup() {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    component = DaggerComponent.builder().fileSystemModule(new FileSystemModule(fileSystem)).build();

  }

  @Test
  public void readableSerialize() {
    Map<String, Object> serialized = ImmutableMap.<String, Object>builder()
        .put("type", "main.types.TestMap")
        .put("extension", ImmutableMap.<String, Object>of())
        .put("children", ImmutableList.builder()
            .add(ImmutableMap.<String, Object>builder()
                .put("func", "stringDoubleMap")
                .put("type", "HanaMapEntry")
                .put("key", ImmutableMap.<String,Object>builder()
                    .put("type", "String")
                    .put("value", "A")
                    .build())
                .put("value", ImmutableMap.<String,Object>builder()
                    .put("type", "Double")
                    .put("value", 10.0)
                    .build())
                .build())
            .build())
        .build();
    component.jsonIo().write(serialized, "/test_map.json", false);

    Object deserialized = component.fileDeserializer().deserializeReadable("/test_map.json");

    assertThat(deserialized).isInstanceOf(TestMap.class);
    TestMap testInteger = (TestMap) deserialized;
    assertThat(testInteger.stringDoubleMap()).containsExactly("A", 10.0);
  }

}
