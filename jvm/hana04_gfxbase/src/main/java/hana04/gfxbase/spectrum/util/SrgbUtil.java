package hana04.gfxbase.spectrum.util;

import hana04.gfxbase.util.MathUtil;

public class SrgbUtil {
    public static void unpackRgba(int rgb, int[] output) {
        output[0] = (rgb >> 16) & 0xff;
        output[1] = (rgb >> 8) & 0xff;
        output[2] = (rgb) & 0xff;
        output[3] = (rgb >> 24) & 0xff;
    }

    public static int packRgba(int[] rgba) {
        int output = 0;
        output += (rgba[2] & 0xff);
        output += (rgba[1] & 0xff) << 8;
        output += (rgba[0] & 0xff) << 16;
        output += (rgba[3] << 24);
        return output;
    }

    public static double linearToSrgb(double linear) {
        linear = MathUtil.clamp(linear, 0, 1);
        if (linear <= 0.0031308) {
            return 12.92 * linear;
        } else {
            return (1+0.055) * Math.pow(linear, 1/2.4) - 0.055;
        }
    }

    public static double srgbToLinear(double srgb) {
        srgb = MathUtil.clamp(srgb, 0, 1);
        if (srgb <= 0.04045) {
            return srgb / 12.92;
        } else {
            return Math.pow((srgb + 0.055)/(1 + 0.055), 2.4);
        }
    }
}
