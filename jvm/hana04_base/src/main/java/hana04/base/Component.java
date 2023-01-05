package hana04.base;

import hana04.base.caching.HanaUnwrapper;
import hana04.base.extension.HanaUberFactory;
import hana04.base.serialize.FileDeserializer;
import hana04.base.serialize.FileSerializer;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.util.JsonIo;
import hana04.base.util.TextIo;

import java.nio.file.FileSystem;

public interface Component {
  HanaUnwrapper unwrapper();

  HanaUberFactory uberFactory();

  FileSystem fileSystem();

  TextIo textIo();

  JsonIo jsonIo();

  ReadableDeserializer.Factory readableDeserializerFactory();

  ReadableSerializer.Factory readableSerializerFactory();

  BinaryDeserializer.Factory binaryDeserializerFactory();

  BinarySerializer.Factory binarySerializerFactory();

  FileDeserializer fileDeserializer();

  FileSerializer fileSerializer();
}
