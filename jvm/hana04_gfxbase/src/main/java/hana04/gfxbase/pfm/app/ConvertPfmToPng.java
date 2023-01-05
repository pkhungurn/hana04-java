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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ConvertPfmToPng {
  public static void main(String[] args) {
    try {
      Options options = new Options();
      options.addOption(Option.builder("e")
        .hasArg()
        .argName("exposure")
        .desc("exposure (aka multiplicative factor)")
        .build());
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      List<String> argList = cmd.getArgList();
      if (cmd.getArgList().size() < 2) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java wakame2.projects.basics.pfm.app.ConvertPfmToPng <pfm-file> <png-file>", options);
        System.exit(0);
      }

      String pfmFile = argList.get(0);
      String pngFile = argList.get(1);
      double exposure = 1;
      if (cmd.hasOption("e")) {
        exposure = Double.valueOf(cmd.getOptionValue("e"));
      }

      Pfm pfm = Pfm.load(pfmFile);
      BufferedImage output = new BufferedImage(pfm.width, pfm.height, BufferedImage.TYPE_INT_ARGB);

      Rgb color = new Rgb();
      int[] rgba = new int[4];
      rgba[3] = 255;
      for (int x = 0; x < pfm.width; x++) {
        for (int y = 0; y < pfm.height; y++) {
          pfm.getColor(x, y, color);
          double r = SrgbUtil.linearToSrgb(color.x * exposure);
          double g = SrgbUtil.linearToSrgb(color.y * exposure);
          double b = SrgbUtil.linearToSrgb(color.z * exposure);
          rgba[0] = (int) (255 * r);
          rgba[1] = (int) (255 * g);
          rgba[2] = (int) (255 * b);
          int value = SrgbUtil.packRgba(rgba);
          output.setRGB(x, pfm.height - y - 1, value);
        }
      }

      ImageIO.write(output, "png", new File(pngFile));
    } catch (Exception e) {
      e.printStackTrace();
    }


    if (args.length < 2) {
      System.out.println("Usage: java wakame2.projects.basics.pfm.app.ConvertPfmToPng <pfm-file> <png-file>");
      System.exit(0);
    }


  }
}
