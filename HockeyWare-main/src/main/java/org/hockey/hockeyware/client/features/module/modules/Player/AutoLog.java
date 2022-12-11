package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.player.InventoryUtil;
import org.hockey.hockeyware.client.util.player.PlayerUtil;

import java.util.Objects;

public class AutoLog extends Module {

    public static final Setting<Integer> health = new Setting<>("Health", 10, 0, 36);
    public static final Setting<Integer> totems = new Setting<>("Totems", 3, 0, 45);
    public static final Setting<Type> typeSetting = new Setting<>("Mode", Type.Health);
    public static final Setting<Boolean> disableOnLog = new Setting<>("DisableOnLog", true);
    String display;
    String spelling;

    public AutoLog() {
        super("AutoLog", "Automatically Logs You Out When You Set It To", Category.Player);
    }

    public int totemCount() {
        return InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING, true);
    }

    @Override
    public String getDisplayInfo() {
        if (typeSetting.getValue().equals(Type.Both)) {
            display = health.getValue() + ", " + totems.getValue();
        }
        if (typeSetting.getValue().equals(Type.Health)) {
            display = health.getValue().toString();
        }
        if (typeSetting.getValue().equals(Type.Totems)) {
            display = totems.getValue().toString();
        }
        return display;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (typeSetting.getValue().equals(Type.Health) && PlayerUtil.getPlayerHealth(mc.player) <= health.getValue()) {
                Objects.requireNonNull(mc.getConnection()).handleDisconnect(new SPacketDisconnect(new TextComponentString("[HockeyWare]: Logged out with " + PlayerUtil.getPlayerHealth(mc.player) + " hp remaining.")));
                if (disableOnLog.getValue()) {
                    this.toggle(true);
                }
            }
            if (typeSetting.getValue().equals(Type.Totems) && InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING, true) <= totems.getValue()) {
                Objects.requireNonNull(mc.getConnection()).handleDisconnect(new SPacketDisconnect(new TextComponentString("[HockeyWare]: Had " + totemCount() + " " + totemSpelling() + " remaining.")));
                if (disableOnLog.getValue()) {
                    this.toggle(true);
                }
            }
            if (typeSetting.getValue().equals(Type.Both) && PlayerUtil.getPlayerHealth(mc.player) <= health.getValue() && InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING, true) <= totems.getValue()) {
                Objects.requireNonNull(mc.getConnection()).handleDisconnect(new SPacketDisconnect(new TextComponentString("[HockeyWare]: Logged out at " + PlayerUtil.getPlayerHealth(mc.player) + " with " + totemCount() + " " + totemSpelling() + " remaining.")));
                if (disableOnLog.getValue()) {
                    this.toggle(true);
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    private String totemSpelling() {
        if (totemCount() == 1) {
            spelling = ("totem");
        } else if (totemCount() > 1) {
            spelling = ("totems");
        }
        return spelling;
    }

    public enum Type {
        Health, Totems, Both
    }
}
