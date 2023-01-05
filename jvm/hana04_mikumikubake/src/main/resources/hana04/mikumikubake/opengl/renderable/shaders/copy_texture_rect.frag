#version 120

varying vec2 geom_texCoord;

uniform sampler2DRect tex_input;

void main() {
    gl_FragColor = texture2DRect(tex_input, geom_texCoord);
}
