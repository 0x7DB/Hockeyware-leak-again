package org.hockey.hockeyware.client.features.module.modules.Chat;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.player.DeathEvent;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

import java.util.Random;

public class AutoEasy extends Module {
    public AutoEasy() {
        super("AutoEasy", "Types Something In Chat When You Kill A Player", Category.Chat);
    }

    public static final Setting<Boolean> greenText = new Setting<>("GreenText", false);

    String[] ezMessages = {
            "EZ <player>! HockeyWare On Top!"
    };

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (event.getPlayer().equals(Globals.mc.player) || getHockey().friendManager.isFriend(event.getPlayer().getUniqueID()) || !fullNullCheck())
            return;
        String randomMessage = (ezMessages[new Random().nextInt(ezMessages.length)]);
        for (int i = 0; i < 1; i++) { //added a for loop that loops once so we don't get more than 1 message
            Globals.mc.player.connection.sendPacket(new CPacketChatMessage((greenText.getValue() ? ">" : "") + randomMessage.replaceAll("<player>", event.getPlayer().getDisplayNameString())));
        }
    }
}
