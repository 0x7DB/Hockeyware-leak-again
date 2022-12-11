#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

float rand(vec2 p){
    return fract(sin(dot(p, vec2(34.4398, 14.3949))) * 1.34);
}

vec2 map(vec3 p){
    // return vec2(length(p) - 1., 1.);
    p.y = -abs(p.y);
    vec3 q = floor(p  );
    float c = rand(q.xz);
    return vec2(p.y + abs(sin(c * 3.+ time)) + 2., 1. - abs(sin(c * 3.+ time)));
}

void main( void ) {

    vec2 p = ( gl_FragCoord.xy * 2. - resolution.xy ) / min(resolution.x, resolution.y);

    vec3 color = vec3(-0.7);

    vec3 cPos = vec3(cos(time * .8), 0., -3. + time);
    vec3 cT = vec3(0., 0., 0. + time);
    vec3 cFwd = normalize(cT - cPos);
    vec3 cUp = normalize(vec3(sin(time * .7)*.3, 1., 0.));

    vec3 cSide = normalize(cross(cUp, cFwd));
    cUp = normalize(cross(cFwd, cSide));
    vec3 rd = (p.x * cSide + p.y * cUp + cFwd);

    vec2 d;
    int k;

    for(int i = 0; i < 300; i++){
        d = map(cPos);
        if(d.x < 0.001){
            break;
        }
        k = i;
        d = min(d, 0.05);
        cPos += rd * d.x;
    }

    if(d.x < 0.001){
        color += d.y * vec3(0.49, 0.0, 0.8) + float(k) / 300.;
    }else{
        color += 0.7;
    }


    gl_FragColor = vec4(color, 1.0 );

}