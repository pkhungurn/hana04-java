package hana04.yuri.postprocessing;

import com.google.common.base.Preconditions;
import hana04.gfxbase.pfm.Pfm;

import javax.vecmath.Vector3d;

public class EdgeAvoidingAtrousFiltering {
  private static double[] filter1d = new double[]{0.0625, 0.25, 0.375, 0.25, 0.0625};

  public static Pfm filter(Pfm colorImage, Pfm positionImage, Pfm normalImage,
                           double colorSigma, double positionSigma, double normalSigma) {
    double[] filter2d = new double[25];
    int[] offsetX = new int[25];
    int[] offsetY = new int[25];
    int pos = 0;
    for (int x = 0; x < 5; x++) {
      for (int y = 0; y < 5; y++) {
        offsetX[pos] = x - 2;
        offsetY[pos] = y - 2;
        filter2d[pos] = filter1d[x] * filter1d[y];
        pos++;
      }
    }

    int width = colorImage.width;
    int height = colorImage.height;
    Preconditions.checkArgument(positionImage.width == width);
    Preconditions.checkArgument(positionImage.height == height);
    Preconditions.checkArgument(normalImage.width == width);
    Preconditions.checkArgument(normalImage.height == height);

    Pfm output = new Pfm(width, height);
    Pfm buffer0 = new Pfm(width, height);
    Pfm buffer1 = new Pfm(width, height);
    Pfm[] buffers = new Pfm[]{buffer0, buffer1};
    int readBuffer = 0;
    int writebuffer = 1;

    Vector3d rgb = new Vector3d();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        colorImage.getColor(x, y, rgb);
        buffer0.setColor(x, y, rgb);
      }
    }

    int stepSize = 1;
    Vector3d pColor = new Vector3d();
    Vector3d pPosition = new Vector3d();
    Vector3d pNormal = new Vector3d();
    Vector3d qColor = new Vector3d();
    Vector3d qPosition = new Vector3d();
    Vector3d qNormal = new Vector3d();
    Vector3d sum = new Vector3d();
    Vector3d bufferValue = new Vector3d();

    while (stepSize < 64) {
      System.out.println("stepSize = " + stepSize);
      // Clear the new buffer.
      {
        pColor.set(0, 0, 0);
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            buffers[writebuffer].setColor(x, y, pColor);
          }
        }
      }
      // Compute the new buffer.
      {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            colorImage.getColor(x, y, pColor);
            positionImage.getColor(x, y, pPosition);
            normalImage.getColor(x, y, pNormal);
            sum.set(0, 0, 0);
            double weightSum = 0;

            for (int i = 0; i < 25; i++) {
              int xx = x + stepSize * offsetX[i];
              int yy = y + stepSize * offsetY[i];
              if (xx < 0 || xx > width-1 || yy < 0 || yy > height-1) {
                continue;
              }

              colorImage.getColor(xx, yy, qColor);
              positionImage.getColor(xx, yy, qPosition);
              normalImage.getColor(xx, yy, qNormal);
              buffers[readBuffer].getColor(xx, yy, bufferValue);

              qColor.sub(pColor);
              qPosition.sub(pPosition);
              qNormal.sub(pNormal);
              double colorDistance = qColor.lengthSquared();
              double positionDistance = qPosition.lengthSquared();
              double normalDistance = qNormal.lengthSquared();

              double colorWeight = Math.exp(-colorDistance / (colorSigma * colorSigma));
              double positionWeight = Math.exp(-positionDistance / (positionSigma * positionSigma));
              double normalWeight = Math.exp(-normalDistance / (normalSigma * normalSigma));

              double weight = colorWeight * positionWeight * normalWeight * filter2d[i];
              //double weight = filter2d[i];
              weightSum += weight;
              sum.scaleAdd(weight, bufferValue, sum);
            }

            sum.scale(1.0 / weightSum);
            buffers[writebuffer].setColor(x, y, sum);
          }
        }
      }
      stepSize *= 2;
      colorSigma /= 2;
      // Swap the buffers.
      readBuffer = 1 - readBuffer;
      writebuffer = 1 - writebuffer;
    }
    // Add the final buffer to the output.
    {
      Vector3d outputBuffer = new Vector3d();
      Vector3d oldBuffer = new Vector3d();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          output.getColor(x, y, outputBuffer);
          buffers[readBuffer].getColor(x, y, oldBuffer);
          outputBuffer.add(oldBuffer);
          output.setColor(x, y, outputBuffer);
        }
      }
    }

    return output;
  }
}
