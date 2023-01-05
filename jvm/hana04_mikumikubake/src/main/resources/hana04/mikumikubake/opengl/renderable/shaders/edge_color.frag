#version 120

uniform vec3 mat_edgeColor;

void main() {
    gl_FragColor = vec4(mat_edgeColor, 1.0);
}
