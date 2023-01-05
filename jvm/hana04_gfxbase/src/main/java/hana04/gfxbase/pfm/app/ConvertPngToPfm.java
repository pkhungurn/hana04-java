/*
 * This file is part of Wakame2, a research-oriented physically-based renderer by Pramook Khungurn.
 *
 * Copyright (c) 2016 by Pramook Khungurn.
 *
 *  Wakame2 is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License Version 3
 *  as published by the Free Software Foundation.
 *
 *  Wakame is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package hana04.gfxbase.pfm.app;

import hana04.gfxbase.pfm.Pfm;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.util.SrgbUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class ConvertPngToPfm {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("java wakame2.projects.basics.pfm.app.ConvertPngToPfm <png-file> <pfm-file>");
      System.exit(0);
    }

    String pngFileName = args[0];
    String pfmFileName = args[1];

    try {
      BufferedImage image = ImageIO.read(new File(pngFileName));
      int width = image.getWidth();
      int height = image.getHeight();
      Pfm pfm = new Pfm(width, height);
      int[] comps = new int[4];
      Rgb c = new Rgb();
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          int rgb = image.getRGB(x, y);
          SrgbUtil.unpackRgba(rgb, comps);
          c.x = SrgbUtil.srgbToLinear(comps[0] / 255.0);
          c.y = SrgbUtil.srgbToLinear(comps[1] / 255.0);
          c.z = SrgbUtil.srgbToLinear(comps[2] / 255.0);
          pfm.setColor(x, height - y - 1, c);
        }
      }

      FileSystem fileSystem = FileSystems.getDefault();
      pfm.save(fileSystem.getPath(pfmFileName));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
