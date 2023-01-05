package hana04.formats.x;

import java.util.List;

public class XUtil {
  public static XTemplateData getFirstMeshData(XFile xFile) {
    XTemplateData meshData = null;
    for (Object item : xFile.getData()) {
      if (item instanceof XTemplateData) {
        XTemplateData templateData = (XTemplateData) item;
        if (templateData.getTemplate().getName().equals("Mesh")) {
          meshData = templateData;
          break;
        }
      }
    }
    return meshData;
  }

  public static boolean checkMeshHasNormal(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshNormals")) {
        return true;
      }
    }
    return false;
  }

  public static boolean checkMeshHasTexCoords(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshTextureCoords")) {
        return true;
      }
    }
    return false;
  }

  public static boolean checkMeshHasMaterials(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshMaterialList")) {
        return true;
      }
    }
    return false;
  }

  public static XTemplateData getNormalData(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshNormals")) {
        return (XTemplateData) meshChildren.get(i);
      }
    }
    return null;
  }

  public static XTemplateData getTexCoordData(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshTextureCoords")) {
        return (XTemplateData) meshChildren.get(i);
      }
    }
    return null;
  }

  public static XTemplateData getMeshMaterialList(XTemplateData mesh) {
    List<Object> meshChildren = (List<Object>) mesh.getValue(4);
    for (int i = 0; i < meshChildren.size(); i++) {
      if (meshChildren.get(i) instanceof XTemplateData
        && ((XTemplateData) meshChildren.get(i)).getTemplate().getName().equals("MeshMaterialList")) {
        return (XTemplateData) meshChildren.get(i);
      }
    }
    return null;
  }
}
