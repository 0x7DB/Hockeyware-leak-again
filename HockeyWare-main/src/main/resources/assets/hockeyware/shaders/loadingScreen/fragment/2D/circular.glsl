#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 resolution;

float aastep(float threshold, float value) {
    #ifdef GL_OES_standard_derivatives
    float afwidth = length(vec2(dFdx(value), dFdy(value))) * 0.70710678118654757;
    return smoothstep(threshold-afwidth, threshold+afwidth, value);
    #else
    return step(threshold, value);

    #endif
}

// Cellular noise ("Worley noise") in 2D in GLSL.
// Copyright (c) Stefan Gustavson 2011-04-19. All rights reserved.
// This code is released under the conditions of the MIT license.
// See LICENSE file for details.

// Permutation polynomial: (34x^2 + x) mod 289
vec3 permute(vec3 x) {
    return mod((34.0 * x + 1.0) * x, 289.0);
}

// Cellular noise, returning F1 and F2 in a vec2.
// Standard 3x3 search window for good F1 and F2 values
vec2 cellular(vec2 P, float seed) {
    #define K 0.142857142857 // 1/7
    #define Ko 0.428571428571 // 3/7
    #define jitter 4. // Less gives more regular pattern
    vec2 Pi = mod(floor(P), 289.0);
    vec2 Pf = fract(P);
    vec3 oi = vec3(-1.0, 0.0, 1.0);
    vec3 of = vec3(-0.5, 0.5, 1.5);
    vec3 px = permute(Pi.x + oi);
    vec3 p = permute(px.x + Pi.y + oi); // p11, p12, p13
    vec3 ox = fract(p*K) - Ko;
    vec3 oy = mod(floor(p*K),7.0)*K - Ko;
    vec3 dx = Pf.x + 0.5 + jitter*ox;
    vec3 dy = Pf.y - of + jitter*oy;
    vec3 d1 = dx * dx + dy * dy; // d11, d12 and d13, squared
    p = permute(px.y + Pi.y + oi); // p21, p22, p23
    ox = fract(p*K) - Ko;
    oy = mod(floor(p*K),7.0)*K - Ko;
    dx = Pf.x - 0.5 + jitter*ox;
    dy = Pf.y - of + jitter*oy;
    vec3 d2 = dx * dx + dy * dy; // d21, d22 and d23, squared
    p = permute(px.z + Pi.y + oi); // p31, p32, p33
    ox = fract(p*K) - Ko;
    oy = mod(floor(p*K),7.0)*K - Ko;
    dx = Pf.x - 1.5 + jitter*ox;
    dy = Pf.y - of + jitter*oy;
    vec3 d3 = dx * dx + dy * dy; // d31, d32 and d33, squared
    // Sort out the two smallest distances (F1, F2)
    vec3 d1a = min(d1, d2);
    d2 = max(d1, d2); // Swap to keep candidates for F2
    d2 = min(d2, d3); // neither F1 nor F2 are now in d3
    d1 = min(d1a, d2); // F1 is now in d1
    d2 = max(d1a, d2); // Swap to keep candidates for F2
    d1.xy = (d1.x < d1.y) ? d1.xy : d1.yx; // Swap if smaller
    d1.xz = (d1.x < d1.z) ? d1.xz : d1.zx; // F1 is in d1.x
    d1.yz = min(d1.yz, d2.yz); // F2 is now not in d2.yz
    d1.y = min(d1.y, d1.z); // nor in  d1.z
    d1.y = min(d1.y, d2.x); // F2 is in d1.y, we're done.
    return sqrt(d1.xy);
}

float facets(vec2 F)
{
    return 0.1 + (F.y - F.x);
}

float blobs(vec2 F)
{
    return 1.0 - F.x * F.x;
}

float rings(vec2 F)
{
    return 1.0 - aastep(0.45, F.x) + aastep(0.55, F.x);
}

vec3 mod289(vec3 x)
{
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x)
{
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

float snoise(vec2 v)
{
    const vec4 C = vec4(0.211324865405187,  // (3.0 - sqrt(3.0)) / 6.0
    0.366025403784439,  // 0.5 * (sqrt(3.0) - 1.0)
    -0.577350269189626,  // -1.0 + 2.0 * C.x
    0.024390243902439);  // 1.0 / 41.0

    // First corner
    vec2 i = floor(v + dot(v, C.yy));
    vec2 x0 = v - i + dot(i, C.xx);

    // Other corners
    vec2 i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;

    // Permutations
    i = mod289(i);  // Avoid truncation effects in permutations
    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0));
    vec3 m = max(0.5 - vec3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
    m = m * m;
    m = m * m;

    // Gradients
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 a0 = x - floor(x + 0.5);

    // Normalize gradients implicitly by scaling m
    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);

    // Compute final noise value at P
    vec3 g;
    g.x = a0.x * x0.x + h.x * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float fbm(vec2 st, float l, float r)
{
    const int octaves = 8;

    float sum = 0.0;
    float f = 0.5;
    float s = 1.0;
    for (int i=0; i < octaves; ++i)
    {
        sum = sum + s * snoise(f * st);
        s = s * r;
        f = f * l;
    }
    return sum;
}

vec3 starfield(vec2 st)
{
    vec2 F = cellular(st, time);

    float value;
    value = 1.0 - F.x;

    value = pow(value, 40.0 + snoise(st * 10.0) * 10.0);
    float alpha = F.y - 0.5;
    // star colors
    vec3 c1 = vec3(.35, .4, .6);
    vec3 c2 = vec3(.3, .5, .8);
    return mix(c1, c2, alpha) * value;
}

vec3 starfield_fractal(vec2 st, float l, float r)
{
    const int octaves = 4;

    vec3 sum = vec3(0, 0, 0);
    float f = .35;
    float s = 2.5;
    for (int i=0; i < octaves; ++i)
    {
        sum = sum + s * starfield(f * st);
        s = s * r;
        f = f * l;
    }
    return sum;
}

void main(void)
{
    vec2 st = ((gl_FragCoord.xy / resolution.xy / vec2(.5, .5)) + vec2(0., 0.) * 10.0);

    vec3 star = starfield_fractal(st * 30.0, 1.4, 0.75);

    float n = fbm(st * 1.0, 2.0, .75);
    vec3 nebula = vec3(n) * 0.14;
    star = star * (n * 0.7 + 1.2);

    float n2 = fbm((st + vec2(132.0, 112.0)) * 1.5, 2.3, 0.6);
    vec3 nebula2 = mix(vec3(1, 0, 0), vec3(0.5, 0.5, 1.), n2) * (n2 - 0.3) * 0.14;

    //gl_FragColor = vec4(nebula + clamp(star, 0.0, 1.0) + nebula2, 1.0);
    gl_FragColor = vec4(nebula2 + clamp(star, 0.0, 1.0), 0.75);
}