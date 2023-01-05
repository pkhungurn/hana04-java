package hana04.yuri.film.zirr;

import com.google.common.base.Preconditions;
import hana04.gfxbase.spectrum.rgb.Rgb;

import javax.vecmath.Tuple3d;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.DoubleBuffer;

class CascadeBuffers {
  private final DoubleBuffer rgbBuffer;
  private final DoubleBuffer countBuffer;
  private final int sizeX;
  private final int sizeY;

  CascadeBuffers(int sizeX, int sizeY) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    Preconditions.checkArgument(sizeX > 0);
    Preconditions.checkArgument(sizeY > 0);
    rgbBuffer = DoubleBuffer.allocate(sizeX * sizeY * 3);
    countBuffer = DoubleBuffer.allocate(sizeX * sizeY);
  }

  public void getRgb(int x, int y, Tuple3d output) {
    output.x = rgbBuffer.get(3 * (y * sizeX + x));
    output.y = rgbBuffer.get(3 * (y * sizeX + x) + 1);
    output.z = rgbBuffer.get(3 * (y * sizeX + x) + 2);
  }


  public double getCount(int x, int y) {
    return countBuffer.get(y * sizeX + x);
  }

  public void addWeightedRgb(int x, int y, Rgb rgb, double weight) {
    double luminance = rgb.getLuminance();

    double rBuf = rgbBuffer.get(3 * (y * sizeX + x));
    double gBuf = rgbBuffer.get(3 * (y * sizeX + x) + 1);
    double bBuf = rgbBuffer.get(3 * (y * sizeX + x) + 2);
    double countBuf = countBuffer.get(y * sizeX + x);

    rBuf += rgb.x * weight;
    gBuf += rgb.y * weight;
    bBuf += rgb.z * weight;
    countBuf += weight;

    rgbBuffer.put(3 * (y * sizeX + x), rBuf);
    rgbBuffer.put(3 * (y * sizeX + x) + 1, gBuf);
    rgbBuffer.put(3 * (y * sizeX + x) + 2, bBuf);
    countBuffer.put(y * sizeX + x, countBuf);
  }

  public void serialize(DataOutputStream stream) {
    try {
      for (int i = 0; i < sizeX * sizeY *3; i++) {
        stream.writeDouble(rgbBuffer.get(i));
      }
      for (int i = 0; i < sizeX * sizeY; i++) {
        stream.writeDouble(countBuffer.get(i));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void deserialize(DataInputStream stream) {
    try {
      for (int i = 0; i < sizeX * sizeY *3; i++) {
        rgbBuffer.put(i, stream.readDouble());
      }
      for (int i = 0; i < sizeX * sizeY; i++) {
        countBuffer.put(i, stream.readDouble());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void put(CascadeBuffers other, int offsetX, int offsetY) {
    Preconditions.checkArgument(offsetX >= 0);
    Preconditions.checkArgument(offsetX + other.sizeX <= sizeX);
    Preconditions.checkArgument(offsetY >= 0);
    Preconditions.checkArgument(offsetY + other.sizeY <= sizeY);
    for (int dy = 0; dy < other.sizeY; dy++) {
      for (int dx = 0; dx < other.sizeX; dx++) {
        int x = offsetX + dx;
        int y = offsetY + dy;

        double rOther = other.rgbBuffer.get(3 * (dy * other.sizeX + dx));
        double gOther = other.rgbBuffer.get(3 * (dy * other.sizeX + dx) + 1);
        double bOther = other.rgbBuffer.get(3 * (dy * other.sizeX + dx) + 2);
        double countOther = other.countBuffer.get(dy * other.sizeX + dx);

        rgbBuffer.put(3 * (y * sizeX + x), rOther);
        rgbBuffer.put(3 * (y * sizeX + x) + 1, gOther);
        rgbBuffer.put(3 * (y * sizeX + x) + 2, bOther);
        countBuffer.put(y * sizeX + x, countOther);
      }
    }
  }
}
