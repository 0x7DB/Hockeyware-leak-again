package org.hockey.hockeyware.client.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.features.Globals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager implements Globals {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onSendPacket(Packet<?> packetIn, CallbackInfo info) {
        if (!fullNullCheck()) return;
        PacketEvent.Send packetSendEvent = new PacketEvent.Send(packetIn);
        HockeyWare.EVENT_BUS.post(packetSendEvent);

        if (packetSendEvent.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onPacketReceive(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo info) {
        if (!fullNullCheck()) return;
        PacketEvent.Receive packetReceiveEvent = new PacketEvent.Receive(packet);
        HockeyWare.EVENT_BUS.post(packetReceiveEvent);

        if (packetReceiveEvent.isCanceled()) {
            info.cancel();
        }
    }
}
