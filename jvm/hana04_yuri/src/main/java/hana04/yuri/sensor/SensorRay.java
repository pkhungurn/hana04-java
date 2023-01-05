package hana04.yuri.sensor;

import hana04.gfxbase.gfxtype.Ray;

public class SensorRay {
  public final Ray ray;
  public double importanceWeight;

  public SensorRay(Ray ray, double importanceWeight) {
    this.ray = ray;
    this.importanceWeight = importanceWeight;
  }
}
