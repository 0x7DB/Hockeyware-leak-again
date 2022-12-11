package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Render.Weather;
import org.hockey.hockeyware.client.mixin.mixins.accessor.IEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer {
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderRainSnow(F)V"))
    public void weatherHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (HockeyWare.INSTANCE.moduleManager.getModuleByClass(Weather.class).isToggled(true)) {
            ((Weather) HockeyWare.INSTANCE.moduleManager.getModuleByClass(Weather.class)).render(partialTicks);
        }
    }
}