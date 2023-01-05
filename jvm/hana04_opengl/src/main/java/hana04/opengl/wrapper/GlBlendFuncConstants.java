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

package hana04.opengl.wrapper;

import java.util.HashMap;

public class GlBlendFuncConstants {
    private final static HashMap<Integer, String> blendFuncIntToString = new HashMap<>();
    private final static HashMap<String, Integer> blendFuncStringToInt = new HashMap<>();

    static {
        blendFuncIntToString.put(GlConstants.GL_ZERO, "zero");
        blendFuncIntToString.put(GlConstants.GL_ONE, "one");
        blendFuncIntToString.put(GlConstants.GL_SRC_COLOR, "srcColor");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_SRC_COLOR, "oneMinusSrcColor");
        blendFuncIntToString.put(GlConstants.GL_DST_COLOR, "dstColor");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_DST_COLOR, "oneMinusDstColor");
        blendFuncIntToString.put(GlConstants.GL_SRC_ALPHA, "srcAlpha");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_SRC_ALPHA, "oneMinusSrcAlpha");
        blendFuncIntToString.put(GlConstants.GL_DST_ALPHA, "dstAlpha");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_DST_ALPHA, "oneMinusDstAlpha");
        blendFuncIntToString.put(GlConstants.GL_CONSTANT_COLOR, "constantColor");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_CONSTANT_COLOR, "oneMinusConstantColor");
        blendFuncIntToString.put(GlConstants.GL_CONSTANT_ALPHA, "constantAlpha");
        blendFuncIntToString.put(GlConstants.GL_ONE_MINUS_CONSTANT_ALPHA, "oneMinusConstantAlpha");
        blendFuncIntToString.put(GlConstants.GL_SRC_ALPHA_SATURATE, "srcAlphaSaturate");

        blendFuncStringToInt.put("zero", GlConstants.GL_ZERO);
        blendFuncStringToInt.put("one", GlConstants.GL_ONE);
        blendFuncStringToInt.put("srcColor", GlConstants.GL_SRC_COLOR);
        blendFuncStringToInt.put("oneMinusSrcColor", GlConstants.GL_ONE_MINUS_SRC_COLOR);
        blendFuncStringToInt.put("dstColor", GlConstants.GL_DST_COLOR);
        blendFuncStringToInt.put("oneMinusDstColor", GlConstants.GL_ONE_MINUS_DST_COLOR);
        blendFuncStringToInt.put("srcAlpha", GlConstants.GL_SRC_ALPHA);
        blendFuncStringToInt.put("oneMinusSrcAlpha", GlConstants.GL_ONE_MINUS_SRC_ALPHA);
        blendFuncStringToInt.put("dstAlpha", GlConstants.GL_DST_ALPHA);
        blendFuncStringToInt.put("oneMinusDstAlpha", GlConstants.GL_ONE_MINUS_DST_ALPHA);
        blendFuncStringToInt.put("constantColor", GlConstants.GL_CONSTANT_COLOR);
        blendFuncStringToInt.put("oneMinusConstantColor", GlConstants.GL_ONE_MINUS_CONSTANT_COLOR);
        blendFuncStringToInt.put("constantAlpha", GlConstants.GL_CONSTANT_ALPHA);
        blendFuncStringToInt.put("oneMinusConstantAlpha", GlConstants.GL_ONE_MINUS_CONSTANT_ALPHA);
        blendFuncStringToInt.put("srcAlphaSaturate", GlConstants.GL_SRC_ALPHA_SATURATE);
    }

    public static int toInt(String name) {
        if (!blendFuncStringToInt.containsKey(name))
            throw new RuntimeException("invalid blend function name '" + name + "'");
        else
            return blendFuncStringToInt.get(name);
    }

    public static String toString(int constant) {
        if (!blendFuncIntToString.containsKey(constant)) {
            throw new RuntimeException("unsupported blend function constant '" + constant + "'");
        } else {
            return blendFuncIntToString.get(constant);
        }
    }
}
