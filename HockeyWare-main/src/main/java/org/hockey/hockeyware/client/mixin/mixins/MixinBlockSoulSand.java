package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.player.SoulSandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand {

    public void onEntityCollidedWithBlock(World world, BlockPos blockPos, IBlockState iBlockState, Entity entity, CallbackInfo info) {
        SoulSandEvent soulSandEvent = new SoulSandEvent();
        HockeyWare.EVENT_BUS.post(soulSandEvent);

        if (soulSandEvent.isCanceled())
            info.cancel();
    }
}