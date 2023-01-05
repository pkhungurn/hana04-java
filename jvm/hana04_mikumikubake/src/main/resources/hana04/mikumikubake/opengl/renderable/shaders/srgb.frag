#version 120

varying vec2 geom_texCoord;

uniform sampler2DRect tex_input;

float to_srgb(float x) {
    x = clamp(x, 0, 1);
    if (x <= 0.0031308) {
        return 12.92 * x;
    } else {
        return (1+0.055) * pow(x, 1/2.4) - 0.055;
    }
}

void main() {
    vec4 value = texture2DRect(tex_input, geom_texCoord);
    vec3 linear = value.rgb;
    float alpha = value.a;
    gl_FragColor = vec4(to_srgb(linear.r), to_srgb(linear.g), to_srgb(linear.b), alpha);
}
