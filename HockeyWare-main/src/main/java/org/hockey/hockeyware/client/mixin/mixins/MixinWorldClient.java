package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.player.EntitySpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    public void onEntityAdded(Entity entity, CallbackInfo info) {
        HockeyWare.EVENT_BUS.post(new EntitySpawnEvent(entity, EntitySpawnEvent.Type.Spawn));
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemoved(Entity entity, CallbackInfo info) {
        HockeyWare.EVENT_BUS.post(new EntitySpawnEvent(entity, EntitySpawnEvent.Type.Despawn));
    }
}
