package hana04.botan.glasset.vertex;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.DirtinessObserver;
import hana04.base.changeprop.DirtinessObserverManager;
import hana04.base.changeprop.VersionManager;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector4d;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class UgPosColVertexAttribAssetExtensions {
  public static class HostAttribData implements HostVertexAttribData {
    public static final GlAttributeSpec POSITION = new GlAttributeSpec("position", 3,
      GlConstants.GL_FLOAT, false, 4 * 7, 0);
    public static final GlAttributeSpec COLOR = new GlAttributeSpec("color", 4,
      GlConstants.GL_FLOAT, false, 4 * 7, 4 * 3);
    private static final HashMap<String, GlAttributeSpec> vertexAttributes = new HashMap<>();

    static {
      vertexAttributes.put("position", POSITION);
      vertexAttributes.put("vert_position", POSITION);
      vertexAttributes.put("vertex_position", POSITION);
      vertexAttributes.put("color", COLOR);
      vertexAttributes.put("vert_color", COLOR);
      vertexAttributes.put("vertex_color", COLOR);
    }

    private ArrayList<Point3d> positions = new ArrayList<>();
    private ArrayList<Vector4d> colors = new ArrayList<>();
    private final VersionManager versionManager = new VersionManager();
    private final DirtinessObserverManager dirtinessObserverManager = new DirtinessObserverManager(this);

    @HanaDeclareExtension(
      extensibleClass = UgPosColVertexAttribAsset.class,
      extensionClass = HostVertexAttribData.class)
    public HostAttribData(UgPosColVertexAttribAsset ugPosColVertexAsset) {
      // NO-OP
    }

    @Override
    public void addObserver(DirtinessObserver observer) {
      dirtinessObserverManager.addObserver(observer);
    }

    @Override
    public void removeObserver(DirtinessObserver observer) {
      dirtinessObserverManager.removeObserver(observer);
    }

    @Override
    public long version() {
      return versionManager.getVersion();
    }

    @Override
    public void update() {
      // NO-OP
    }

    @Override
    public boolean isDirty() {
      return false;
    }

    public class Builder {
      private Vector4d currentColor = new Vector4d();
      private ArrayList<Point3d> builderPositions = new ArrayList<>();
      private ArrayList<Vector4d> builderColors = new ArrayList<>();

      public Builder() {
        builderPositions.clear();
        builderColors.clear();
        currentColor.set(1.0, 1.0, 1.0, 1.0);
      }

      public Builder addPosition(double x, double y, double z) {
        builderPositions.add(new Point3d(x, y, z));
        builderColors.add(new Vector4d(currentColor));
        return this;
      }

      public Builder addPosition(Tuple3d p) {
        return addPosition(p.x, p.y, p.z);
      }

      public Builder setColor(double r, double g, double b, double a) {
        currentColor.set(r, g, b, a);
        return this;
      }

      public Builder setColor(Tuple4d c) {
        return setColor(c.x, c.y, c.z, c.w);
      }

      public HostAttribData endBuild() {
        synchronized (HostAttribData.this) {
          HostAttribData.this.positions = builderPositions;
          HostAttribData.this.colors = builderColors;
        }
        versionManager.bumpVersion();
        dirtinessObserverManager.notifyObservers();
        return HostAttribData.this;
      }
    }

    public Builder startBuild() {
      return new Builder();
    }

    public synchronized int getVertexCount() {
      return positions.size();
    }

    @Override
    public synchronized Buffer getBuffer() {
      FloatBuffer buffer =
        ByteBuffer.allocateDirect(getBufferSizeInByte()).order(ByteOrder.nativeOrder()).asFloatBuffer();
      for (int i = 0; i < getVertexCount(); i++) {
        buffer.put(7 * i + 0, (float) positions.get(i).x);
        buffer.put(7 * i + 1, (float) positions.get(i).y);
        buffer.put(7 * i + 2, (float) positions.get(i).z);
        buffer.put(7 * i + 3, (float) colors.get(i).x);
        buffer.put(7 * i + 4, (float) colors.get(i).y);
        buffer.put(7 * i + 5, (float) colors.get(i).z);
        buffer.put(7 * i + 6, (float) colors.get(i).w);
      }
      return buffer;
    }

    @Override
    public int getBufferSizeInByte() {
      return getVertexCount() * 7 * 4;
    }

    @Override
    public GlAttributeSpec getAttributeSpec(String name) {
      return vertexAttributes.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
      return vertexAttributes.containsKey(name);
    }

    @Override
    public int getNumBytesPerVertex() {
      return 7 * 4;
    }
  }

  public static class HostDataBuilder {
    private final HostAttribData hostData;

    @HanaDeclareExtension(
      extensionClass = HostDataBuilder.class,
      extensibleClass = UgPosColVertexAttribAsset.class)
    public HostDataBuilder(UgPosColVertexAttribAsset ugPosColVertexAsset) {
      hostData = (HostAttribData) ugPosColVertexAsset.getExtension(HostVertexAttribData.class);
    }

    public HostAttribData.Builder startBuild() {
      return hostData.startBuild();
    }
  }
}
