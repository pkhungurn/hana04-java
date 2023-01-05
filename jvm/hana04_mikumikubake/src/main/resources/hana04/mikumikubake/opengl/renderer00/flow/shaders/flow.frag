#version 150

in vec3 geom_startPosition;
in vec3 geom_endPosition;
in vec2 geom_texCoord;

uniform bool mat_hasTexture;
uniform sampler2D mat_texture;
uniform float mat_alpha;
uniform mat4 sys_projectionMatrix;
uniform mat4 sys_viewMatrix;

uniform int output_width;
uniform int output_height;
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

    vec4 startClipPosition = sys_projectionMatrix * (sys_viewMatrix * vec4(geom_startPosition, 1));
    vec4 startNdc = startClipPosition / startClipPosition.w;
    vec2 positionMapTexCoord = (startNdc.xy + vec2(1, 1)) / 2.0 * vec2(positionMap_width, positionMap_height);
    vec4 positionMapValue = texture(positionMap_texture, positionMapTexCoord);

    if (positionMapValue.w == 0) {
        discard;
    }
    if (length(geom_startPosition - positionMapValue.xyz) > positionMap_epsilon) {
        discard;
    }

    vec4 endClipPosition = sys_projectionMatrix * (sys_viewMatrix * vec4(geom_endPosition, 1));
    vec4 endNdc = endClipPosition / endClipPosition.w;
    vec2 scaling_factor = vec2((output_width-1)*1.0/(output_width), (output_height-1)*1.0/output_height);
    vec2 diff = (startNdc - endNdc).xy * scaling_factor;
    FragColor = vec4(diff, 1, 1);
}