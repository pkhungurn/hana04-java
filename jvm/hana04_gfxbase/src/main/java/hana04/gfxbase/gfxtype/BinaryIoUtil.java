package hana04.gfxbase.gfxtype;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;
import javax.vecmath.Tuple4i;
import java.io.DataOutputStream;
import java.io.IOException;

public class BinaryIoUtil {
  public static void readTuple2f(SwappedDataInputStream fin, Tuple2f v) throws IOException {
    v.x = fin.readFloat();
    v.y = fin.readFloat();
  }

  public static void readTuple3f(SwappedDataInputStream fin, Tuple3f v) throws IOException {
    v.x = fin.readFloat();
    v.y = fin.readFloat();
    v.z = fin.readFloat();
  }

  public static void readTuple4f(SwappedDataInputStream fin, Tuple4f v) throws IOException {
    v.x = fin.readFloat();
    v.y = fin.readFloat();
    v.z = fin.readFloat();
    v.w = fin.readFloat();
  }

  public static void readTuple2d(SwappedDataInputStream fin, Tuple2d v) throws IOException {
    v.x = fin.readDouble();
    v.y = fin.readDouble();
  }

  public static void readTuple3d(SwappedDataInputStream fin, Tuple3d v) throws IOException {
    v.x = fin.readDouble();
    v.y = fin.readDouble();
    v.z = fin.readDouble();
  }

  public static void readTuple4d(SwappedDataInputStream fin, Tuple4d v) throws IOException {
    v.x = fin.readDouble();
    v.y = fin.readDouble();
    v.z = fin.readDouble();
    v.w = fin.readDouble();
  }

  public static void readTuple4i(SwappedDataInputStream fin, Tuple4i v) throws IOException {
    v.x = fin.readInt();
    v.y = fin.readInt();
    v.z = fin.readInt();
    v.w = fin.readInt();
  }

  public static void writeLittleEndianTuple3f(DataOutputStream fout, Tuple3f t) throws IOException {
    BinaryIo.writeLittleEndianFloat(fout, t.x);
    BinaryIo.writeLittleEndianFloat(fout, t.y);
    BinaryIo.writeLittleEndianFloat(fout, t.z);
  }

  public static void writeLittleEndianTuple4f(DataOutputStream fout, Tuple4f t) throws IOException {
    BinaryIo.writeLittleEndianFloat(fout, t.x);
    BinaryIo.writeLittleEndianFloat(fout, t.y);
    BinaryIo.writeLittleEndianFloat(fout, t.z);
    BinaryIo.writeLittleEndianFloat(fout, t.w);
  }

  public static void writeLittleEndianTuple2d(DataOutputStream fout, Tuple2d t) throws IOException {
    BinaryIo.writeLittleEndianDouble(fout, t.x);
    BinaryIo.writeLittleEndianDouble(fout, t.y);
  }

  public static void writeLittleEndianTuple3d(DataOutputStream fout, Tuple3d t) throws IOException {
    BinaryIo.writeLittleEndianDouble(fout, t.x);
    BinaryIo.writeLittleEndianDouble(fout, t.y);
    BinaryIo.writeLittleEndianDouble(fout, t.z);
  }

  public static void writeLittleEndianTuple3i(DataOutputStream fout, Tuple3i t) throws IOException {
    BinaryIo.writeLittleEndianInt(fout, t.x);
    BinaryIo.writeLittleEndianInt(fout, t.y);
    BinaryIo.writeLittleEndianInt(fout, t.z);
  }

  public static void writeLittleEndianTuple4d(DataOutputStream fout, Tuple4d t) throws IOException {
    BinaryIo.writeLittleEndianDouble(fout, t.x);
    BinaryIo.writeLittleEndianDouble(fout, t.y);
    BinaryIo.writeLittleEndianDouble(fout, t.z);
    BinaryIo.writeLittleEndianDouble(fout, t.w);
  }

  public static void writeLittleEndianTuple4i(DataOutputStream fout, Tuple4i t) throws IOException {
    BinaryIo.writeLittleEndianInt(fout, t.x);
    BinaryIo.writeLittleEndianInt(fout, t.y);
    BinaryIo.writeLittleEndianInt(fout, t.z);
    BinaryIo.writeLittleEndianInt(fout, t.w);
  }
}
