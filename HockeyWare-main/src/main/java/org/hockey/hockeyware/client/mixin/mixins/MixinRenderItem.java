package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.renderer.RenderItem;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Render.EnchantGlintModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"))
    private int renderEffectRenderModel(final int glintColor) {
        if (HockeyWare.INSTANCE.moduleManager.getModuleByClass(EnchantGlintModifier.class).isToggled(true))
            return EnchantGlintModifier.glintColor.getValue().getRGB();
        else
            return glintColor;
    }

    @ModifyArgs(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void renderEffectScale(Args args) {
        if (HockeyWare.INSTANCE.moduleManager.getModuleByClass(EnchantGlintModifier.class).isToggled(true)) {
            float scale = EnchantGlintModifier.glintScale.getValue();
            args.set(0, scale);
            args.set(1, scale);
            args.set(2, scale);
        }
    }

    @ModifyArgs(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"))
    private void renderEffectTranslate(Args args) {
        if (HockeyWare.INSTANCE.moduleManager.getModuleByClass(EnchantGlintModifier.class).isToggled(true))
            args.set(0, (float) args.get(0) * EnchantGlintModifier.glintSpeed.getValue());
    }
}