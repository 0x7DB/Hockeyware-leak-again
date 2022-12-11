package org.hockey.hockeyware.client.features.module.modules.Client;


import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class MainMenu extends Module {
    public static MainMenu INSTANCE;

    public static final Setting<Backround> backround = new Setting<>("Backround", Backround.GreyGalaxy);

    public MainMenu() {
        super("MainMenu", "Controls custom screens used by the client", Category.Client);
        INSTANCE = this;
    }

    public static MainMenu getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainMenu();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
    }

    public enum Backround {
        GreyGalaxy, New

    }
}
