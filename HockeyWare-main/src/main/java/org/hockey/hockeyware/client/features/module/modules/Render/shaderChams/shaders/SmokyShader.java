package org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.shaders;

import org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class SmokyShader extends FramebufferShader {
    public static SmokyShader Smoky_Shader;
    public float time;
    public float timeMult = 0.001f;

    public SmokyShader() {
        super("smoky.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), time);
        time += timeMult * animationSpeed;
    }

    static {
        Smoky_Shader = new SmokyShader();
    }
}