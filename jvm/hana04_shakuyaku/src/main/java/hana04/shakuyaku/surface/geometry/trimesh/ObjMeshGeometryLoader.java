package hana04.shakuyaku.surface.geometry.trimesh;

import hana04.base.filesystem.FilePath;
import hana04.base.util.TextIo;
import hana04.gfxbase.gfxtype.Aabb3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjMeshGeometryLoader {
  private static final Logger logger = LoggerFactory.getLogger(ObjMeshGeometryLoader.class);

  public static TriangleMeshInfo load(FilePath filePath, FileSystem fileSystem) {
    long start = System.currentTimeMillis();
    String resolvedPath = filePath.storedPath;
    Path path = fileSystem.getPath(resolvedPath);
    logger.info("Loading a mesh from \"{}\" ...", path);
    String fileContent = TextIo.readTextFile(path);
    ConcreteTriangleMeshInfo.Builder builder = new ConcreteTriangleMeshInfo.Builder();
    parse(fileContent, builder);
    TriangleMeshInfo triangleMeshData = builder.build();
    long elapsed = System.currentTimeMillis() - start;
    String timingMessage = String.format("Done loading mesh with %d vertices and %d triangles. " +
            "Took %d min(s) %d second(s) and %d ms.",
        triangleMeshData.getVertexCount(), triangleMeshData.getTriangleCount(),
        elapsed / (60 * 1000), (elapsed / 1000) % 60, elapsed % 1000);
    logger.info(timingMessage);
    return triangleMeshData;
  }

  private static void parse(String content, ConcreteTriangleMeshInfo.Builder builder) {
    InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    BufferedReader fin = new BufferedReader(new InputStreamReader(inputStream));

    ArrayList<Point3d> P = new ArrayList<>();
    ArrayList<Vector2d> T = new ArrayList<>();
    ArrayList<Vector3d> N = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();
    ArrayList<OBJVertex> vertices = new ArrayList<>();
    HashMap<OBJVertex, Integer> vertexMap = new HashMap<>();

    Aabb3d aabb = new Aabb3d();
    aabb.reset();

    try {
      String line = fin.readLine();
      while (line != null) {
        line = line.trim();
        if (line.length() == 0) {
          line = fin.readLine();
          continue;
        }
        if (line.charAt(0) == '#') {
          line = fin.readLine();
          continue;
        }

        int spacePos = line.indexOf(' ');
        if (spacePos == -1) {
          line = fin.readLine();
          continue;
        }
        String prefix = line.substring(0, spacePos);
        String theRest = line.substring(spacePos + 1).trim();
        switch (prefix) {
          case "v": {
            String[] comps = theRest.split("\\s+");
            Point3d p = new Point3d(
                Double.parseDouble(comps[0]),
                Double.parseDouble(comps[1]),
                Double.parseDouble(comps[2]));
            aabb.expandBy(p);
            P.add(p);
            break;
          }
          case "vt": {
            String[] comps = theRest.split("\\s+");
            Vector2d t = new Vector2d(
                Double.parseDouble(comps[0]),
                Double.parseDouble(comps[1]));
            T.add(t);
            break;
          }
          case "vn": {
            String[] comps = theRest.split("\\s+");
            Vector3d n = new Vector3d(
                Double.parseDouble(comps[0]),
                Double.parseDouble(comps[1]),
                Double.parseDouble(comps[2]));
            N.add(n);
            break;
          }
          case "f": {
            String[] comps = theRest.split("\\s+");
            OBJVertex[] verts = new OBJVertex[comps.length];
            for (int i = 0; i < verts.length; i++) {
              verts[i] = new OBJVertex(comps[i]);
            }

            OBJVertex[] triVerts = new OBJVertex[3];
            for (int i = 0; i < verts.length - 2; i++) {
              triVerts[0] = verts[0];
              triVerts[1] = verts[i + 1];
              triVerts[2] = verts[i + 2];

              for (int j = 0; j < 3; j++) {
                if (!vertexMap.containsKey(triVerts[j])) {
                  indices.add(vertices.size());
                  vertexMap.put(triVerts[j], vertices.size());
                  vertices.add(triVerts[j]);
                } else {
                  indices.add(vertexMap.get(triVerts[j]));
                }
              }
            }
            break;
          }
        }
        line = fin.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < indices.size() / 3; i++) {
      builder.addTriangle(
          indices.get(3 * i),
          indices.get(3 * i + 1),
          indices.get(3 * i + 2));
    }

    for (OBJVertex vertice1 : vertices) {
      Point3d p = P.get(vertice1.p - 1);
      builder.addPosition(new Point3d(p));
    }

    if (!N.isEmpty()) {
      for (OBJVertex vertice : vertices) {
        Vector3d n = N.get(vertice.n - 1);
        builder.addNormal(new Vector3d(n));
      }
    }

    if (!T.isEmpty()) {
      for (OBJVertex vertice : vertices) {
        Vector2d t = T.get(vertice.uv - 1);
        builder.addTexCoord(new Vector2d(t));
      }
    }
  }

  private static class OBJVertex {
    public int p = -1;
    int n = -1;
    public int uv = -1;

    public OBJVertex() {
      // NO-OP
    }

    OBJVertex(String s) {
      String[] comps = s.split("/");

      if (comps.length < 1 || comps.length > 3) {
        throw new RuntimeException("Invalid vertex asData: " + s);
      }

      p = Integer.parseInt(comps[0]);

      if (comps.length >= 2 && comps[1].length() > 0) {
        uv = Integer.parseInt(comps[1]);
      }

      if (comps.length >= 3 && comps[2].length() > 0) {
        n = Integer.parseInt(comps[2]);
      }
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof OBJVertex) {
        OBJVertex other = (OBJVertex) o;
        return other.p == p && other.n == n && other.uv == uv;
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      int hash = Integer.hashCode(p);
      hash = hash * 37 + Integer.hashCode(uv);
      hash = hash * 37 + Integer.hashCode(n);
      return hash;
    }
  }
}
