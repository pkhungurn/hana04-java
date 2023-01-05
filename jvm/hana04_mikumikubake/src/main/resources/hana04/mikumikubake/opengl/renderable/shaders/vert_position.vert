#version 120

attribute vec3 vert_position;

uniform mat4 sys_modelMatrix;
uniform mat4 sys_viewMatrix;
uniform mat4 sys_projectionMatrix;

void main() {
    vec4 worldPosition = sys_modelMatrix * vec4(vert_position,1);
    vec4 cameraPosition = sys_viewMatrix * vec4(worldPosition);
    vec4 clipPosition = sys_projectionMatrix * vec4(cameraPosition);
    gl_Position = clipPosition;
}