package hana04.formats.mmd.generic.api;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Optional;

public interface MmdMaterial {
  String japaneseName();

  String englishName();

  Vector4f diffuse();

  Vector3f specular();

  float shininess();

  Vector3f ambient();

  Optional<? extends EdgeInfo> edgeInfo();

  Optional<String> getTextureFileName();

  int vertexStart();

  int vertexCount();

  interface EdgeInfo {
    Vector4f edgeColor();

    float edgeSize();
  }
}
