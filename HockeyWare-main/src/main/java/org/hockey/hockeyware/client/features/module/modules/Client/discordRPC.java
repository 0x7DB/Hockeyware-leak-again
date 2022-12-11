package org.hockey.hockeyware.client.features.module.modules.Client;

import org.hockey.hockeyware.client.features.RPC;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class discordRPC extends Module {

    public static final Setting<Boolean> showIP = new Setting<>("Show IP", true);

    public static discordRPC INSTANCE = new discordRPC();

    public discordRPC() {
        super("DiscordRPC", "Enables HockeyWare Discord Rich Presence", Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        RPC.start();
    }

    @Override
    public void onDisable() {
        RPC.stop();
    }
}