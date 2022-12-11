package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.player.RenderHeldItemEvent;
import org.hockey.hockeyware.client.features.module.modules.Render.ViewModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

//    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
//    private void onRenderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo ci) {
//        if (ViewModel.isHand())
//            ViewModel.INSTANCE.hand(hand == EnumHand.MAIN_HAND ? EnumHandSide.RIGHT : EnumHandSide.LEFT);
//    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void onTransformFirstPersonPre(EnumHandSide handSide, float p_187459_2_, CallbackInfo info) {
        HockeyWare.EVENT_BUS.post(new RenderHeldItemEvent.Pre(handSide));
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void onTransformFirstPersonPost(EnumHandSide handSide, float p_187459_2_, CallbackInfo info) {
        HockeyWare.EVENT_BUS.post(new RenderHeldItemEvent.Post(handSide));
    }

    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"), cancellable = true)
    public void onTransformSideFirstPerson(EnumHandSide handSide, float p_187459_2_, CallbackInfo info) {
        final RenderHeldItemEvent.Pre renderHeldItemEvent = new RenderHeldItemEvent.Pre(handSide);
        HockeyWare.EVENT_BUS.post(renderHeldItemEvent);

        if (renderHeldItemEvent.isCanceled())
            info.cancel();
    }
}
