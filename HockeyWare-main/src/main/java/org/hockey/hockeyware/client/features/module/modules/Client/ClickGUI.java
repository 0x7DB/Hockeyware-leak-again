package org.hockey.hockeyware.client.features.module.modules.Client;

import org.hockey.hockeyware.client.gui.Click;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.ColorSetting;
import org.hockey.hockeyware.client.setting.Setting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGUI extends Module {

    public static final Setting<Color> color = new ColorSetting("Color", new Color(255, 0, 0, 255));
    public static final Setting<Boolean> description = new Setting<>("Description", true);
    public static final Setting<Boolean> showBind = new Setting("Show-Bind", false);
    public static final Setting<Boolean> size = new Setting("Category-Size", false);

    public static final Setting<Integer> descriptionWidth = new Setting<>("Description-Width", 240, 100, 1000);

    public static final Setting<Boolean> blur = new Setting("Blur", false);
    public static final Setting<Integer> blurAmount = new Setting<>("Blur-Amount", 8, 1, 20);
    public static final Setting<Integer> blurSize = new Setting<>("Blur-Size", 3, 1, 20);

    public static ClickGUI INSTANCE;

    public ClickGUI() {
        super("ClickGUI", "Shows This Screen", Category.Client, Keyboard.KEY_MINUS);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        if (!fullNullCheck()) {
            toggle(true);
            return;
        }

        Click gui = Click.getGUIINSTANCE();
        gui.init();
        gui.onGuiOpened();
        mc.displayGuiScreen(gui);
    }

    @Override
    protected void onDisable() {
        if (fullNullCheck()) {
            Globals.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onUpdate() {
        if (Globals.mc.currentScreen == null) {
            toggle(true);
        }
    }
}