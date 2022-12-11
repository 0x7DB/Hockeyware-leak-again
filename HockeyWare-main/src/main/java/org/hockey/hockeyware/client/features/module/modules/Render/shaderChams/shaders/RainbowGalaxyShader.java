package org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.shaders;

import org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class RainbowGalaxyShader extends FramebufferShader {
    public static RainbowGalaxyShader RainbowGalaxy_Shader;
    public float time;
    public float timeMult = 0.01f;

    public RainbowGalaxyShader() {
        super("rainbowGalaxy.frag");
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
        RainbowGalaxy_Shader = new RainbowGalaxyShader();
    }
}