package org.hockey.hockeyware.client.features.module.modules.Render;

import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.ColorSetting;
import org.hockey.hockeyware.client.setting.Setting;

import java.awt.*;

public class EnchantGlintModifier extends Module {
    public static final Setting<Float> glintScale = new Setting<>("Glint Size", 0.1f, 5.0f, 20.0f);
    public static final Setting<Float> glintSpeed = new Setting<>("Glint Speed", 0.1f, 1.0f, 10.0f);
    public static final Setting<Color> glintColor = new ColorSetting("Glint Color", Color.RED);

    public EnchantGlintModifier INSTANCE;

    public EnchantGlintModifier() {
        super("EnchantGlintModifier", "Allows You To Change The Glint Of Held Items", Category.Render);
        INSTANCE = this;
    }
}