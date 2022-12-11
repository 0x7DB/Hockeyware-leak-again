package org.hockey.hockeyware.client.features.module.modules.Player;

import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class ViewLock extends Module {
    public ViewLock() {
        super("ViewLock", "Locks Your View", Category.Player);
    }

    public static final Setting<Mode> hand = new Setting<>("Mode", Mode.Both);
    public static final Setting<Float> yaw = new Setting<>("Yaw", 1f, 0, 8);
    public static final Setting<Float> pitch = new Setting<>("Pitch", 1f, -90, 90);

    @Override
    public void onUpdate() {
        if (hand.getValue().equals(Mode.Yaw) || hand.getValue().equals(Mode.Both)) {
            mc.player.rotationYaw = yaw.getValue() * 45f;
        }
        if (hand.getValue().equals(Mode.Pitch) || hand.getValue().equals(Mode.Both)) {
            mc.player.rotationPitch = pitch.getValue();
        }
    }

    public enum Mode {
        Both, Yaw, Pitch
    }
}