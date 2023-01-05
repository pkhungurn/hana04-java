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

public class GlPrimitiveConstants {
    public static String toString(int primitive) {
        switch (primitive) {
            case GlConstants.GL_POINTS:
                return "points";
            case GlConstants.GL_LINES:
                return "lines";
            case GlConstants.GL_TRIANGLES:
                return "triangles";
            default:
                throw new RuntimeException("invalid primitive type");
        }
    }

    public static int toInt(String s) {
        s = s.toLowerCase();
        switch (s) {
            case "points":
                return GlConstants.GL_POINTS;
            case "lines":
                return GlConstants.GL_LINES;
            case "triangles":
                return GlConstants.GL_TRIANGLES;
            default:
                throw new RuntimeException("invalid primitive string: " + s);
        }
    }
}
