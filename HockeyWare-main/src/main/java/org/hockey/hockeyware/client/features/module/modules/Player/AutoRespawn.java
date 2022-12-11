package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.ClientMessage;

public class AutoRespawn extends Module {
    public static final Setting<Boolean> deathCoords = new Setting<>("DeathCoords", false);

    public AutoRespawn() {
        super("AutoRespawn", "Allows You To Automatically Respawn", Category.Player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // before a gui change event can be canceled
    public void onDisplayDeathScreen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            if (deathCoords.getValue()) {
                ClientMessage.sendOverwriteClientMessage("You Died At: " + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ);
            }
            mc.player.respawnPlayer();
        }
    }
}
