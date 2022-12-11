package org.hockey.hockeyware.client.features;

import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();

    default boolean fullNullCheck() {
        return mc.world != null || mc.player != null;
    }

    default HockeyWare getHockey() {
        return HockeyWare.INSTANCE;
    }
}
