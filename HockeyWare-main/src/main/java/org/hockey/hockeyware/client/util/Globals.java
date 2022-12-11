package org.hockey.hockeyware.client.util;

import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();

    default boolean fullNullCheck() {
        return mc.player != null && mc.world != null;
    }

    default HockeyWare getHockey() {
        return HockeyWare.INSTANCE;
    }
}
