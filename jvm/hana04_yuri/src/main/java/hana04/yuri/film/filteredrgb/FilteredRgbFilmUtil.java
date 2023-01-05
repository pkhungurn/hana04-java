package hana04.yuri.film.filteredrgb;

import javax.vecmath.Vector4d;

class FilteredRgbFilmUtil {
  static Vector4d[][] allocateStorage(int sizeX, int sizeY, int borderSize) {
    Vector4d[][] data = new Vector4d[sizeY + 2*borderSize][];
    for (int y = 0; y < data.length; y++) {
      data[y] = new Vector4d[sizeX + 2*borderSize];
    }
    for (int y = 0; y < data.length; y++) {
      for (int x = 0; x < data[y].length; x++) {
        data[y][x] = new Vector4d(0, 0, 0, 0);
      }
    }
    return data;
  }
}
