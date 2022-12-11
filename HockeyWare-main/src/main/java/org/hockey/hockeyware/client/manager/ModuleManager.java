package org.hockey.hockeyware.client.manager;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Chat.AutoEasy;
import org.hockey.hockeyware.client.features.module.modules.Chat.ChatAppend;
import org.hockey.hockeyware.client.features.module.modules.Chat.PopLag;
import org.hockey.hockeyware.client.features.module.modules.Client.*;
import org.hockey.hockeyware.client.features.module.modules.Combat.*;
import org.hockey.hockeyware.client.features.module.modules.Misc.FakePlayer;
import org.hockey.hockeyware.client.features.module.modules.Misc.MiddleClick;
import org.hockey.hockeyware.client.features.module.modules.Misc.PingSpoof;
import org.hockey.hockeyware.client.features.module.modules.Movement.*;
import org.hockey.hockeyware.client.features.module.modules.Player.*;
import org.hockey.hockeyware.client.features.module.modules.Render.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleManager implements Globals {
    public final ArrayList<Module> modules;

    public ModuleManager() {
        modules = Lists.newArrayList(
                // Chat Modules
                new AutoEasy(),
                new ChatAppend(),
                new PopLag(),

                // Client Modules
                new Capes(),
                new ClickGUI(),
                new CustomFont(),
                new discordRPC(),
                new HUD(),
                new IRC(),
                new Notifier(),
                new Preferences(),
                new MainMenu(),

                // Combot Modules
                new Aura(),
                new AutoBowRelease(),
                new AutoCrystal(), //Fix Stuff
                new AutoTotem(),
                new Burrow(),

                // Misc Modules
                new FakePlayer(),
                new MiddleClick(),
                new PingSpoof(),

                // Movement Modules
                new AutoWalk(),
                new NoSlow(),
                new PacketFly(),
                new Speed(),
                new Sprint(),
                new Step(),
                new Velocity(),

                // Player Modules
                new AntiVoid(),
                new AutoArmor(),
                new AutoLog(),
                new AutoRespawn(),
                new ChestSwap(),
                new FastEXP(),
                new Replenish(),
                new ViewLock(),

                // Render Modules
                new Animations(),
                new CrystalModifier(),
                new EnchantGlintModifier(),
                new Fullbright(),
                new LightningEffect(),
                new Nametags(),
                new ShaderChams(),
                new ViewModel(),
                new Weather()
        );
        modules.forEach(Module::register);
    }

    public Module getModuleByClass(Class<? extends Module> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) return module;
        }
        return null;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int code = Keyboard.getEventKey();
        if (mc.currentScreen == null && code != Keyboard.KEY_NONE && !Keyboard.getEventKeyState()) {
            for (Module module : modules) {
                if (module.getKeybind() == code) {
                    module.toggle(false);
                }
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}