#version 150

in vec3 geom_startPosition;
in vec2 geom_texCoord;

uniform bool mat_hasTexture;
uniform sampler2D mat_texture;
uniform float mat_alpha;
uniform mat4 sys_projectionMatrix;
uniform mat4 sys_viewMatrix;

uniform int positionMap_width;
uniform int positionMap_height;
uniform float positionMap_epsilon;
uniform sampler2DRect positionMap_texture;

out vec4 FragColor;

void main() {
    vec4 textureValue = vec4(1, 1, 1, 1);
    if (mat_hasTexture) {
        textureValue = texture(mat_texture, geom_texCoord);
    }
    float alpha = mat_alpha * textureValue.a;
    if (alpha < 0.0000001) {
        discard;
    }

    vec4 clipPosition = sys_projectionMatrix * (sys_viewMatrix * vec4(geom_startPosition, 1));
    vec4 ndc = clipPosition / clipPosition.w;
    vec2 positionMapTexCoord = (ndc.xy + vec2(1,1)) / 2.0 * vec2(positionMap_width, positionMap_height);
    vec4 positionMapValue = texture(positionMap_texture, positionMapTexCoord);
    if (positionMapValue.w == 0) {
        discard;
    }
    if (length(geom_startPosition - positionMapValue.xyz) > positionMap_epsilon) {
        discard;
    }
    FragColor = vec4(1,1,1,1);
}