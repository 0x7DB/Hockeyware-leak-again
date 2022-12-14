package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import org.hockey.hockeyware.client.util.client.IShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author Gerald
 * @since 6/14/2021
 **/

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements IShaderGroup {

    @Accessor(value = "listFramebuffers")
    public abstract List<Framebuffer> getListFramebuffers();

    @Accessor(value = "listShaders")
    public abstract List<Shader> getListShaders();
}
