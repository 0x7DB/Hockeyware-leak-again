package org.hockey.hockeyware.client.features.module.modules.Render;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class Fullbright extends Module {
    public static final Setting<Enum> mode = new Setting<>("Mode", lightMode.Gamma);
    public static float oldGamma = -1.0f;

    public Fullbright() {
        super("Fullbright", "Allows You To Change The Lighting Of Your Game", Category.Render);
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        if (oldGamma != -1.0f) {
            mc.gameSettings.gammaSetting = oldGamma;
            oldGamma = -1.0f;
        }

        if (mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == lightMode.Gamma) {
            if (oldGamma == -1.0f) {
                oldGamma = mc.gameSettings.gammaSetting;
            }

            if (mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
            }

            mc.gameSettings.gammaSetting = 100.0f;
        } else if (mode.getValue() == lightMode.Potion) {
            if (oldGamma != -1.0f) {
                mc.gameSettings.gammaSetting = oldGamma;
                oldGamma = -1.0f;
            }

            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 1000000000));
        }
    }

    public enum lightMode {
        /**
         * Changes the game's gamma value directly
         */
        Gamma,

        /**
         * Adds the NIGHT_VISION potion effect infinitely to the player
         */
        Potion
    }
}