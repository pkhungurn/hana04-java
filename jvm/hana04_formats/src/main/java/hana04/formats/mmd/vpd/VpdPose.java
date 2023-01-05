
package hana04.formats.mmd.vpd;

import com.google.auto.value.AutoValue;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VpdPose {
  private final Map<String, Vector3f> boneDisplacements = new HashMap<String, Vector3f>();
  private final Map<String, Quat4f> boneRotations = new HashMap<String, Quat4f>();
  private final Map<String, Float> morphWeights = new HashMap<String, Float>();

  public VpdPose() {
    // NO-OP
  }

  public boolean hasBone(String name) {
    return boneDisplacements.containsKey(name);
  }

  public boolean hasMorph(String name) {
    return morphWeights.containsKey(name);
  }

  public void getBonePose(String name, Vector3f displacement, Quat4f rotation) {
    if (!boneDisplacements.containsKey(name)) {
      displacement.set(0, 0, 0);
      rotation.set(0, 0, 0, 1);
    } else {
      displacement.set(boneDisplacements.get(name));
      rotation.set(boneRotations.get(name));
    }
  }

  public BonePose getBonePose(String name) {
    Quat4f rotation = new Quat4f();
    Vector3f displacement = new Vector3f();
    getBonePose(name, displacement, rotation);
    return BonePose.create(displacement, rotation);
  }

  public Set<String> boneNames() {
    return boneDisplacements.keySet();
  }

  public Set<String> morphNames() {
    return morphWeights.keySet();
  }

  public float getMorphWeight(String name) {
    return morphWeights.getOrDefault(name, 0.0f);
  }

  public void clobber() {
    boneDisplacements.clear();
    boneRotations.clear();
    morphWeights.clear();
  }

  public void clear() {
    clearBonePoses();
    clearMorphWeights();
  }

  public void clearBonePoses() {
    for (Map.Entry<String, Vector3f> entry : boneDisplacements.entrySet()) {
      entry.getValue().set(0, 0, 0);
    }
    for (Map.Entry<String, Quat4f> entry : boneRotations.entrySet()) {
      entry.getValue().set(0, 0, 0, 1);
    }
  }

  public void clearMorphWeights() {
    morphWeights.replaceAll((k, v) -> (float) 0);
  }

  public void copy(VpdPose other) {
    clear();
    for (Map.Entry<String, Vector3f> entry : other.boneDisplacements.entrySet()) {
      String boneName = entry.getKey();
      if (boneDisplacements.containsKey(boneName)) {
        boneDisplacements.get(boneName).set(entry.getValue());
      } else {
        boneDisplacements.put(boneName, new Vector3f(entry.getValue()));
      }
    }
    for (Map.Entry<String, Quat4f> entry : other.boneRotations.entrySet()) {
      String boneName = entry.getKey();
      if (boneRotations.containsKey(boneName)) {
        boneRotations.get(boneName).set(entry.getValue());
      } else {
        boneRotations.put(boneName, new Quat4f(entry.getValue()));
      }
    }
    for (String key : other.morphWeights.keySet()) {
      morphWeights.put(key, other.morphWeights.get(key));
    }
  }

  public void read(BufferedReader reader) throws IOException {
    clobber();

    String firstLine = reader.readLine();
    if (!firstLine.trim().equals("Vocaloid Pose Data file")) {
      throw new RuntimeException("magic is wrong");
    }

    reader.readLine();
    reader.readLine();
    String[] comps = reader.readLine().split(";");
    int boneCount = Integer.parseInt(comps[0]);
    reader.readLine();

    String[] lines = new String[5];
    for (int i = 0; i < boneCount; i++) {
      for (int j = 0; j < 5; j++) {
        lines[j] = reader.readLine();
      }
      String[] components = lines[0].split("\\{");
      String boneName = components[1].trim();

      components = lines[1].split("[,;]");
      Vector3f boneDisplacement = new Vector3f(
          Float.parseFloat(components[0]),
          Float.parseFloat(components[1]),
          Float.parseFloat(components[2]));

      components = lines[2].split("[,;]");
      Quat4f boneRotation = new Quat4f(
          Float.parseFloat(components[0]),
          Float.parseFloat(components[1]),
          Float.parseFloat(components[2]),
          Float.parseFloat(components[3]));

      boneDisplacements.put(boneName, boneDisplacement);
      boneRotations.put(boneName, boneRotation);
    }
  }

  public static VpdPose load(String fileName) throws IOException {
    VpdPose result = new VpdPose();

    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
    BufferedReader reader = new BufferedReader(isr);

    result.read(reader);

    return result;
  }

  public static VpdPose load(Path path) {
    try {
      VpdPose result = new VpdPose();
      InputStream fis = Files.newInputStream(path);
      InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
      BufferedReader reader = new BufferedReader(isr);
      result.read(reader);
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setBonePose(String boneName, Tuple3f displacement, Tuple4f rotation) {
    if (boneDisplacements.containsKey(boneName)) {
      boneDisplacements.get(boneName).set(displacement);
    } else {
      Vector3f d = new Vector3f();
      d.set(displacement);
      boneDisplacements.put(boneName, d);
    }

    if (boneRotations.containsKey(boneName)) {
      boneRotations.get(boneName).set(rotation);
    } else {
      Quat4f q = new Quat4f();
      q.set(rotation);
      boneRotations.put(boneName, q);
    }
  }

  public void setMorphWeight(String morphName, float weight) {
    morphWeights.put(morphName, weight);
  }

  public static void interpolate(VpdPose p0, VpdPose p1, float alpha, VpdPose out) {
    Vector3f d0 = new Vector3f();
    Vector3f d1 = new Vector3f();
    Vector3f da = new Vector3f();
    Quat4f q0 = new Quat4f();
    Quat4f q1 = new Quat4f();
    Quat4f qa = new Quat4f();

    out.clear();
    for (String boneName : p0.boneNames()) {
      p0.getBonePose(boneName, d0, q0);
      p1.getBonePose(boneName, d1, q1);
      da.interpolate(d0, d1, alpha);
      qa.interpolate(q0, q1, alpha);
      out.setBonePose(boneName, da, qa);
    }
    for (String boneName : p1.boneNames()) {
      if (!p0.hasBone(boneName)) {
        p0.getBonePose(boneName, d0, q0);
        p1.getBonePose(boneName, d1, q1);
        da.interpolate(d0, d1, alpha);
        qa.interpolate(q0, q1, alpha);
        out.setBonePose(boneName, da, qa);
      }
    }
    for (String morphName : p0.morphNames()) {
      float m0 = p0.getMorphWeight(morphName);
      float m1 = p1.getMorphWeight(morphName);
      float ma = (1 - alpha) * m0 + alpha * m1;
      out.setMorphWeight(morphName, ma);
    }
    for (String morphName : p1.morphNames()) {
      if (!p0.hasMorph(morphName)) {
        float m0 = p0.getMorphWeight(morphName);
        float m1 = p1.getMorphWeight(morphName);
        float ma = (1 - alpha) * m0 + alpha * m1;
        out.setMorphWeight(morphName, ma);
      }
    }
  }

  @AutoValue
  public static abstract class BonePose {
    public abstract Vector3f displacement();

    public abstract Quat4f rotation();

    public static BonePose create(Vector3f displacement, Quat4f rotation) {
      return new AutoValue_VpdPose_BonePose(displacement, rotation);
    }
  }
}
