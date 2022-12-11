package org.hockey.hockeyware.client.features.module.modules.Render;

import net.minecraft.entity.player.EntityPlayer;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class Animations extends Module {

    public static final Setting<Boolean> crouch = new Setting<>("Crouch", true);
    public static final Setting<Boolean> noLimbSwing = new Setting<>("NoLimbSwing", true);
    public static Animations INSTANCE;

    public Animations() {
        super("Animations", "Lets You Change Player Animations", Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (crouch.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.equals(mc.player)) continue;
                player.setSneaking(true);
            }
        }
    }

    @Override
    public void onRender3D() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.equals(mc.player)) continue;
            if (noLimbSwing.getValue()) {
                player.limbSwing = 0;
                player.limbSwingAmount = 0;
                player.prevLimbSwingAmount = 0;
            }
        }
    }
}
