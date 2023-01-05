#version 150

in vec3 geom_position;
in vec2 geom_texCoord;

uniform bool mat_hasTexture;
uniform sampler2D mat_texture;
uniform float mat_alpha;

out vec4 FragColor;

void main() {
    vec4 textureValue = vec4(1, 1, 1, 1);
    if (mat_hasTexture) {
        textureValue = texture(mat_texture, geom_texCoord);
    }
    float alpha = mat_alpha * textureValue.a;

    if (alpha < 0.0000001) {
        discard;
    } else {
        FragColor = vec4(geom_position, 1);
    }
}