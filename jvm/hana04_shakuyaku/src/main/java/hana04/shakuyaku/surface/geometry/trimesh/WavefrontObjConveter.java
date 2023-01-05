package hana04.shakuyaku.surface.geometry.trimesh;

import hana04.base.util.TextIo;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.nio.file.Path;

public class WavefrontObjConveter {
  public static void convert(TriangleMeshInfo triangleMeshInfo, Path path) {
    StringBuilder content = new StringBuilder();

    int vertexCount = triangleMeshInfo.getVertexCount();
    Point3d position = new Point3d();
    for (int i = 0; i < vertexCount; i++) {
      triangleMeshInfo.getPosition(i, position);
      content.append(String.format("v %f %f %f\n", position.x, position.y, position.z));
    }
    Vector2d texCoord = new Vector2d();
    for (int i = 0; i < vertexCount; i++) {
      triangleMeshInfo.getTexCoord(i, texCoord);
      content.append(String.format("vt %f %f\n", texCoord.x, texCoord.y));
    }
    Vector3d normal = new Vector3d();
    for (int i = 0; i < vertexCount; i++) {
      triangleMeshInfo.getNormal(i, normal);
      content.append(String.format("vn %f %f %f\n", normal.x, normal.y, normal.z));
    }

    int triangleCount = triangleMeshInfo.getTriangleCount();
    Point3i vertexIndices = new Point3i();
    for (int i = 0; i < triangleCount; i++) {
      triangleMeshInfo.getTriangleVertexIndices(i, vertexIndices);
      content.append(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d\n",
        vertexIndices.x+1, vertexIndices.x+1, vertexIndices.x+1,
        vertexIndices.y+1, vertexIndices.y+1, vertexIndices.y+1,
        vertexIndices.z+1, vertexIndices.z+1, vertexIndices.z+1));
    }

    TextIo.writeTextFile(path, content.toString());
  }
}
