package org.hockey.hockeyware.client.features.module.modules.Movement;

import net.minecraft.client.settings.KeyBinding;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

import static org.hockey.hockeyware.client.features.Globals.mc;

public class AutoWalk extends Module {

    public static final Setting<Boolean> sprint = new Setting<>("Sprint", true);
    public static AutoWalk INSTANCE;

    public AutoWalk() {
        super("AutoWalk", "Allows You To Automatically Walk", Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        if (sprint.getValue()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }
}