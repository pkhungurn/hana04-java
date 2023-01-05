#version 120

attribute vec3 vert_position;
attribute vec2 vert_texCoord;

varying vec2 geom_texCoord;

void main() {
    geom_texCoord = vert_texCoord;
    gl_Position = vec4(vert_position, 1);
}
