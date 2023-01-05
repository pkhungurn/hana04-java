#version 120

varying vec2 geom_texCoord;

uniform sampler2D tex_input;

void main() {
    gl_FragColor = texture2D(tex_input, geom_texCoord);
}
