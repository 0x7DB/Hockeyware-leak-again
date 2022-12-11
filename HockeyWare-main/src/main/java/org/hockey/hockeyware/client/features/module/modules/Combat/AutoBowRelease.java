package org.hockey.hockeyware.client.features.module.modules.Combat;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.Globals;

public class AutoBowRelease extends Module {
    public static final Setting<Integer> ticks = new Setting<>("Ticks", 3, 0, 20);

    public AutoBowRelease() {
        super("AutoBowRelease", "Automatically Releases Your Bow For You", Category.Combat);
    }

    @Override
    public void onUpdate() {
        if (Globals.mc.player.getHeldItemMainhand().getItem() == Items.BOW && Globals.mc.player.isHandActive() && Globals.mc.player.getItemInUseMaxCount() > ticks.getValue()) {
            Globals.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Globals.mc.player.getHorizontalFacing()));
            Globals.mc.player.stopActiveHand();
        }
    }
}
