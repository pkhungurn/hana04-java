#version 120

varying vec2 geom_texCoord;

uniform vec4 tex_scale;
uniform sampler2DRect tex_input;

void main() {
    gl_FragColor = tex_scale * texture2DRect(tex_input, geom_texCoord);
}
