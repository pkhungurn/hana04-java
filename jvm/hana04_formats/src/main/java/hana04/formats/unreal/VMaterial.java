
package hana04.formats.unreal;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VMaterial {
  public String name;
  public int textureIndex;  // Texture index ('multiskin index')
  public int polyFlags;     // ALL poly's with THIS material will have this flag.
  public int auxMaterial;   // Reserved: index into another material, eg. detailtexture/shininess/whatever.
  public int auxFlags;      // Reserved: auxiliary flags
  public int lodBias;       // V3Material-specific lod bias (unused)
  public int lodStyle;      // V3Material-specific lod style (unused)

  public void read(SwappedDataInputStream fin, String directory) throws IOException {
    name = BinaryIo.readUtfString(fin, 64);
    textureIndex = fin.readInt();
    polyFlags = fin.readInt();
    auxMaterial = fin.readInt();
    auxFlags = fin.readInt();
    lodBias = fin.readInt();
    lodStyle = fin.readInt();
  }

  public static Map<String, String> readMaterialInstanceConstantFile(String fileName) throws IOException {
    File file = new File(fileName);
    FileReader fileReader = new FileReader(file);
    BufferedReader fin = new BufferedReader(fileReader);
    String directory = file.getAbsoluteFile().getParent();

    HashMap<String, String> result = new HashMap<String, String>();

    while (true) {
      String line = fin.readLine();
      if (line != null) {
        line = line.trim();
        String[] comps = line.split("=");
        if (comps.length != 2) {
          continue;
        } else {
          result.put(comps[0], directory + "/" + comps[1] + ".tga");
        }
      } else {
        break;
      }
    }

    fin.close();
    return result;
  }
}
