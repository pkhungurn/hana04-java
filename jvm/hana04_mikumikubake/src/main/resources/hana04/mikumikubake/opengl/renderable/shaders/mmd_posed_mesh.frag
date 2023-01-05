#version 150

in vec3 geom_normal;
in vec2 geom_texCoord;

uniform vec3 mat_ambient;
uniform vec3 mat_diffuse;
uniform bool mat_hasTexture;
uniform sampler2D mat_texture;
uniform float mat_alpha;

uniform vec3 ambientLight_radiance;
uniform vec3 dirLight_radiance;
uniform vec3 dirLight_direction;

out vec4 FragColor;

void main() {
    vec3 n = normalize(geom_normal);

    vec4 textureValue = vec4(1,1,1,1);
    if (mat_hasTexture) {
        textureValue = texture(mat_texture, geom_texCoord);
    }
    float alpha = mat_alpha * textureValue.a;
    vec3 ambient = mat_ambient * textureValue.xyz;
    vec3 diffuse = mat_diffuse * textureValue.xyz;

    float cosTheta = max(dot(n, dirLight_direction), 0);
    /*
    if (cosTheta > 0.2) {
        cosTheta = 0.3 + smoothstep(0.29, 0.3, cosTheta) * 0.7;
    } else if (cosTheta > 0.05) {
        cosTheta = 0.1 + smoothstep(0.09, 0.1, cosTheta) * 0.2;
    } else {
        cosTheta = 0.1;
    }
    */

    vec4 fragColor = vec4(ambient * ambientLight_radiance + diffuse * cosTheta * dirLight_radiance, alpha);

    if (fragColor.a < 0.0000001) {
        discard;
    } else {
        FragColor = fragColor;
        //FragColor = vec4((geom_normal + 1) / 2, mat_alpha);
    }
}