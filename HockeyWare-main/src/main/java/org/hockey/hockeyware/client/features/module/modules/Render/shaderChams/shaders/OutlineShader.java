package org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.shaders;

import org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.FramebufferShader;
import org.lwjgl.opengl.GL20;

public final class OutlineShader extends FramebufferShader {
    public static final OutlineShader Outline_Shader = new OutlineShader();
    public float radius, quality, red,  green, blue, alpha;

    public OutlineShader() {
        super("outline.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth * (radius * quality), 1F / mc.displayHeight * (radius * quality));
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1f(getUniform("radius"), radius);
    }
}