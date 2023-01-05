package hana04.yuri.emitter.envmap;

import hana04.gfxbase.util.MathUtil;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Point2i;
import javax.vecmath.Tuple2i;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class GridMercatorProjection {
  private final int width;
  private final int height;

  public GridMercatorProjection(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public double solidAngle(int x, int y) {
    double phiExtent = 2 * Math.PI / width;
    double cosTheta0 = Math.cos(Math.PI - Math.PI * y / height);
    double cosTheta1 = Math.cos(Math.PI - Math.PI * (y + 1) / height);
    double thetaExtent = cosTheta1 - cosTheta0;
    return phiExtent * thetaExtent;
  }

  public Vector3d sampleDirectionInPixel(int x, int y, RandomNumberGenerator rng) {
    double phi = 2 * Math.PI * ((x + rng.next1D()) / width);
    double cosTheta0 = Math.cos(Math.PI - Math.PI * y / height);
    double cosTheta1 = Math.cos(Math.PI - Math.PI * (y + 1) / height);
    double totalArea = cosTheta1 - cosTheta0;
    double cosTheta = cosTheta1 - rng.next1D() * totalArea;
    double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
    return new Vector3d(sinTheta * Math.cos(phi), sinTheta * Math.sin(phi), cosTheta);
  }

  public Tuple2i getPixelCoord(Tuple3d v) {
    double phi = Math.atan2(v.y, v.x);
    if (phi < 0)
      phi += 2 * Math.PI;
    int x = (int) Math.floor(MathUtil.clamp((phi / (2 * Math.PI)) * width, 0, width - 1));
    int y = (int) Math.floor(MathUtil.clamp(height - Math.acos(v.z) / Math.PI * height, 0, height - 1));
    return new Point2i(x,y);
  }
}
