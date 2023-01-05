package hana04.shakuyaku.surface.mmd.pmxbase;

import hana04.base.filesystem.FilePath;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxVertex;
import hana04.shakuyaku.surface.geometry.trimesh.ConcreteTriangleMeshInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class PmxBaseGeometryLoader {
  private static final Logger logger = LoggerFactory.getLogger(PmxBaseGeometryLoader.class);

  public static TriangleMeshInfo load(FilePath filePath, FileSystem fileSystem) {
    try {
      long start = System.currentTimeMillis();
      String resolvedPath = filePath.storedPath;
      Path path = fileSystem.getPath(resolvedPath);
      logger.info("Loading a PMX file from \"{}\" ...", path);
      PmxModel pmxModel = PmxModel.load(path);
      TriangleMeshInfo data = createTriMesh(pmxModel);
      long elapsed = System.currentTimeMillis() - start;
      String timingMessage = String.format("Done loading mesh with %d vertices and %d triangles. " +
          "Took %d min(s) %d second(s) and %d ms.",
        data.getVertexCount(), data.getTriangleCount(),
        elapsed / (60 * 1000), (elapsed / 1000) % 60, elapsed % 1000);
      logger.info(timingMessage);
      return data;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot not PMX file");
    }
  }

  public static TriangleMeshInfo createTriMesh(PmxModel model) {
    ConcreteTriangleMeshInfo.Builder builder = new ConcreteTriangleMeshInfo.Builder();
    for (int i = 0; i < model.getVertexCount(); i++) {
      PmxVertex vertex = model.getVertex(i);
      builder.addPosition(vertex.position.x, vertex.position.y, -vertex.position.z);
      builder.addNormal(vertex.normal.x, vertex.normal.y, -vertex.normal.z);
      builder.addTexCoord(vertex.texCoords.x, 1 - vertex.texCoords.y);
    }
    for (int i = 0; i < model.getTriangleCount(); i++) {
      int v0 = model.getVertexIndex(3 * i);
      int v1 = model.getVertexIndex(3 * i + 1);
      int v2 = model.getVertexIndex(3 * i + 2);
      builder.addTriangle(v0, v2, v1);
    }
    return builder.build();
  }
}
