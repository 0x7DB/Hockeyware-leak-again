package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import org.hockey.hockeyware.client.features.module.modules.crystalmodifier.CrystalModelHandler;
import org.hockey.hockeyware.client.mixin.mixins.accessor.AccessorRenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 19:22 of 23.10.2022
 */
@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(RenderManager renderManagerIn, CallbackInfo ci) {
        ((AccessorRenderEnderCrystal) this).modelEnderCrystal(new CrystalModelHandler(true));
        ((AccessorRenderEnderCrystal) this).modelEnderCrystalNoBase(new CrystalModelHandler(false));
    }
}