package org.hockey.hockeyware.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hockey.hockeyware.client.features.module.modules.Client.*;
import org.hockey.hockeyware.client.gui.mainMenu.customMainMenu;
import org.hockey.hockeyware.client.manager.*;
import org.hockey.hockeyware.client.manager.friend.FriendManager;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public class HockeyWare {
    public static final String NAME = "HockeyWare";
    public static final String VERSION = "Beta 1.4.2";
    public static final Logger LOGGER = LogManager.getLogger("hockeyware");

    public static final HockeyWare INSTANCE = new HockeyWare();
    public customMainMenu customMainScreen;
    public ModuleManager moduleManager;
    public TickManager getTickManager;
    public static FontManager fontManager;
    public CommandManager commandManager;
    public FriendManager friendManager;
    public CapeManager capeManager;
    public EventManager eventManager;
    public InventoryManager getInventoryManager;
    public RotationManager getRotationManager;
    public TotemPopManager popManager;

    public static boolean initialized = false;

    public static EventBus EVENT_BUS = MinecraftForge.EVENT_BUS;

    public void init() {
        if (initialized) return;

        EVENT_BUS = MinecraftForge.EVENT_BUS;

        EVENT_BUS.register(EventManager.getInstance());

        fontManager = new FontManager();
        EVENT_BUS.register(fontManager);

        moduleManager = new ModuleManager();
        EVENT_BUS.register(moduleManager);

        getTickManager = new TickManager();
        EVENT_BUS.register(getTickManager);

        commandManager = new CommandManager();
        EVENT_BUS.register(commandManager);

        eventManager = new EventManager();
        EVENT_BUS.register(eventManager);

        friendManager = new FriendManager();
        getInventoryManager = new InventoryManager();
        getRotationManager = new RotationManager();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Saving Config...");
            ConfigManager.saveConfig();
        }));

        ConfigManager.loadConfig();

        capeManager = new CapeManager();

        popManager = new TotemPopManager();
        EVENT_BUS.register(popManager);

        customMainScreen = new customMainMenu();

        Display.setTitle(NAME + " " + VERSION);

        initialized = true;


        try {
            if (!ConfigManager.hasRan()) {
                Capes.INSTANCE.toggle(true);
                HUD.INSTANCE.toggle(true);
                discordRPC.INSTANCE.toggle(true);
                IRC.getInstance().toggle(true);
                Notifier.INSTANCE.toggle(true);
                Preferences.INSTANCE.toggle(true);
            }
        } catch (IOException ignored) {
        }
    }
}