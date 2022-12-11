#version 120

uniform sampler2D texture;
uniform sampler2D sampler;
uniform vec2 texelSize;
uniform float radius;
uniform vec2 resolution;
uniform float time;

void main(void) {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if(centerCol.a != 0) {
        gl_FragColor=texture2D(sampler, gl_TexCoord[0].xy);
    } else {
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));
                if (currentColor.a != 0) {
                    gl_FragColor=vec4(1);
                }
            }
        }
    }
}