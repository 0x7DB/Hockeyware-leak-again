package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.world.BlockUtil;

public class AntiVoid extends Module {
    public AntiVoid() {
        super("AntiVoid", "Lags You Back When You Get To Y Level 0", Category.Player);
    }

    public static final Setting<ReturnMode> returnMode = new Setting<>("Mode", ReturnMode.Move);

    @Override
    public String getDisplayInfo() {
        return returnMode.getValue().name();
    }

    @Override
    public void onUpdate() {
        if (mc.player.posY < 1 && BlockUtil.getBlock(new BlockPos(mc.player.posX, 0, mc.player.posZ)).equals(Blocks.AIR)) {
            mc.player.motionY = 0;
            if (returnMode.getValue().equals(ReturnMode.None)) {
                if (mc.player.moveForward > 0) {
                    mc.player.motionY = 0.15;
                }
            }
            if (returnMode.getValue().equals(ReturnMode.Move)) {
                mc.player.motionY = 0.15;
            } else if (returnMode.getValue().equals(ReturnMode.Jump)) {
                mc.player.jump();
            }
        }
    }

    public enum ReturnMode {
        Move, Jump, None
    }
}
