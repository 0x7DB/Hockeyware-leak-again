package org.hockey.hockeyware.client.features.module.modules.Movement;

import net.minecraft.client.settings.KeyBinding;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;


public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Allows You To Automatically Sprint", Category.Movement);
    }

    @Override
    public void onUpdate() {
        if (!mc.player.isSprinting()) {{
                    if (mc.player.isSneaking() || mc.player.getFoodStats().getFoodLevel() <= 6 || mc.player.collidedHorizontally || mc.player.isHandActive()) {
                    }

                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                }
            }
        }
    }