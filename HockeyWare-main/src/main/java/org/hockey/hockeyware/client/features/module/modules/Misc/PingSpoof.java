package org.hockey.hockeyware.client.features.module.modules.Misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.mixin.mixins.accessor.ICPacketConfirmTransaction;
import org.hockey.hockeyware.client.mixin.mixins.accessor.ICPacketKeepAlive;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.Timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingSpoof extends Module {
    public static PingSpoof INSTANCE;

    public PingSpoof() {
        super("PingSpoof", "Spoofs Your Latency To The Server", Category.Misc);
        INSTANCE = this;
    }

    public static Setting<Double> delay = new Setting<>("Delay", 0.1, 0.5, 5.0);

    public static Setting<Boolean> reduction = new Setting<>("Reduction", false);

    public static Setting<Boolean> transactions = new Setting<>("Transactions", false);

    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();

    private final Timer packetTimer = new Timer();

    @Override
    public void onUpdate() {

        if (packetTimer.passedTime(delay.getValue().longValue(), Timer.Format.SECONDS)) {

            if (!packets.isEmpty()) {

                packets.forEach(packet -> {
                    if (packet != null) {

                        if (reduction.getValue()) {
                            if (packet instanceof CPacketKeepAlive) {
                                ((ICPacketKeepAlive) packet).setKey(-1);
                            }

                            else if (packet instanceof CPacketConfirmTransaction) {
                                ((ICPacketConfirmTransaction) packet).setUid((short) -6);
                            }
                        }

                        mc.player.connection.sendPacket(packet);
                    }
                });

                packets.clear();
            }

            packetTimer.resetTime();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (!packets.isEmpty()) {

            packets.forEach(packet -> {
                if (packet != null) {
                    mc.player.connection.sendPacket(packet);
                }
            });

            packets.clear();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {

        if (event.getPacket() instanceof CPacketKeepAlive || (transactions.getValue() && event.getPacket() instanceof CPacketConfirmTransaction)) {

            if (!packets.contains(event.getPacket())) {

                event.setCanceled(true);
                packets.add(event.getPacket());
            }
        }
    }
}