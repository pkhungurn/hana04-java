package main.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import hana04.base.filesystem.FileSystemModule;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.util.UuidUtil;
import main.Component;
import main.DaggerComponent;
import main.TypeIds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import java.io.DataOutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static hana04.base.serialize.binary.BinarySerializer.TYPE_TAG;
import static hana04.base.serialize.binary.BinarySerializer.UUID_TAG;
import static hana04.base.serialize.binary.BinarySerializer.VALUE_TAG;

@RunWith(JUnit4.class)
public class TestIntegerTest {
  private Component component;

  @Before
  public void setup() {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    component = DaggerComponent.builder().fileSystemModule(new FileSystemModule(fileSystem)).build();

  }

  @Test
  public void readableSerialize() {
    Map<String, Object> serialized = ImmutableMap.<String, Object>builder()
        .put("type", "main.types.TestInteger")
        .put("extension", ImmutableMap.<String, Object>of())
        .put("children", ImmutableList.builder()
            .add(ImmutableMap.<String, Object>builder()
                .put("func", "intConst")
                .put("type", "Integer")
                .put("value", 20)
                .build())
            .build())
        .build();
    component.jsonIo().write(serialized, "/test_integer.json", false);

    Object deserialized = component.fileDeserializer().deserializeReadable("/test_integer.json");

    assertThat(deserialized).isInstanceOf(TestInteger.class);
    TestInteger testInteger = (TestInteger) deserialized;
    assertThat(testInteger.intConst()).isEqualTo(20);
  }

  @Test
  public void readableSerialize_defaultValue() {
    Map<String, Object> serialized = ImmutableMap.<String, Object>builder()
        .put("type", "main.types.TestInteger")
        .put("extension", ImmutableMap.<String, Object>of())
        .put("children", ImmutableList.of())
        .build();
    component.jsonIo().write(serialized, "/test_integer_default_value.json", false);

    Object deserialized = component.fileDeserializer().deserializeReadable("/test_integer_default_value.json");

    assertThat(deserialized).isInstanceOf(TestInteger.class);
    TestInteger testInteger = (TestInteger) deserialized;
    assertThat(testInteger.intConst()).isEqualTo(10);
  }

  @Test
  public void binarySerialize() throws Exception {
    Path path = component.fileSystem().getPath("/test_integer.msgpck");
    DataOutputStream fout = new DataOutputStream(Files.newOutputStream(path));
    MessagePacker packer = MessagePack.newDefaultPacker(fout);
    packer.packMapHeader(3);
    {
      packer.packInt(TYPE_TAG);
      packer.packInt(TypeIds.TYPE_ID_MAIN_TYPES_TEST_INTEGER);
    }
    {
      packer.packInt(VALUE_TAG);
      packer.packMapHeader(2);
      packer.packInt(1);
      {
        packer.packMapHeader(2);
        packer.packInt(TYPE_TAG);
        packer.packInt(hana04.serialize.TypeIds.TYPE_ID_INTEGER);
        packer.packInt(VALUE_TAG);
        packer.packInt(20);
      }
      packer.packInt(BinarySerializer.EXTENSION_TAG);
      packer.packArrayHeader(0);
    }
    {
      UUID uuid = UUID.randomUUID();
      packer.packInt(UUID_TAG);
      UuidUtil.pack(packer, uuid);
    }
    packer.flush();
    fout.flush();
    fout.close();

    Object deserialized = component.fileDeserializer().deserializeBinary("/test_integer.msgpck");

    assertThat(deserialized).isInstanceOf(TestInteger.class);
    TestInteger testInteger = (TestInteger) deserialized;
    assertThat(testInteger.intConst()).isEqualTo(20);
  }

  @Test
  public void binarySerialize_defaultValue() throws Exception {
    Path path = component.fileSystem().getPath("/test_integer_default_value.msgpck");
    DataOutputStream fout = new DataOutputStream(Files.newOutputStream(path));
    MessagePacker packer = MessagePack.newDefaultPacker(fout);
    packer.packMapHeader(3);
    {
      packer.packInt(TYPE_TAG);
      packer.packInt(TypeIds.TYPE_ID_MAIN_TYPES_TEST_INTEGER);
    }
    {
      packer.packInt(VALUE_TAG);
      packer.packMapHeader(1);
      packer.packInt(BinarySerializer.EXTENSION_TAG);
      packer.packArrayHeader(0);
    }
    {
      UUID uuid = UUID.randomUUID();
      packer.packInt(UUID_TAG);
      UuidUtil.pack(packer, uuid);
    }
    packer.flush();
    fout.flush();
    fout.close();

    Object deserialized = component.fileDeserializer().deserializeBinary("/test_integer_default_value.msgpck");

    assertThat(deserialized).isInstanceOf(TestInteger.class);
    TestInteger testInteger = (TestInteger) deserialized;
    assertThat(testInteger.intConst()).isEqualTo(10);
  }
}
