package org.hockey.hockeyware.client.mixin.mixins;

import org.hockey.hockeyware.client.events.player.EventModelPlayerRender;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.model.ModelBase;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { ModelPlayer.class }, priority = 94355)
public class ModelPlayerMixin
{
    @Shadow
    public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
    }

    @Inject(method = { "render" }, at = { @At("HEAD") }, cancellable = true)
    private void renderPre(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        final EventModelPlayerRender modelrenderpre = new EventModelPlayerRender((ModelBase)ModelPlayer.class.cast(this), entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, 0);
        MinecraftForge.EVENT_BUS.post((Event)modelrenderpre);
        if (modelrenderpre.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = { "render" }, at = { @At("RETURN") })
    private void renderPost(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        final EventModelPlayerRender modelrenderpost = new EventModelPlayerRender((ModelBase)ModelPlayer.class.cast(this), entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, 1);
        MinecraftForge.EVENT_BUS.post((Event)modelrenderpost);
    }
}
