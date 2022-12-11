package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Items;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;

/**
 * @author Cubic & hockey
 * @since 23.10.2022
 */
public class FastEXP extends Module {


    public FastEXP() {
        super("FastEXP", "Allows You To Throw XP Fast", Category.Player);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null)
            return;

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() == Items.EXPERIENCE_BOTTLE) {
            mc.rightClickDelayTimer = 0;
        }

    }
}
