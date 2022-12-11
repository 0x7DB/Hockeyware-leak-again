package org.hockey.hockeyware.client.events.client;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.hockey.hockeyware.client.features.module.Module;

public class ModuleToggleEvent extends Event {

    public final Module module;

    public ModuleToggleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}
