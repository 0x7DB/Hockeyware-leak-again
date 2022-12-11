package org.hockey.hockeyware.client.features.command.commands;

import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.util.client.ClientMessage;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "Allows You To Bind Modules To A Key", "bind" + " " + "[module]" + " " + "[key]");
    }

    @Override
    public void runCommand(List<String> args) {
        if (args.size() >= 2) {
            for (Module module : HockeyWare.INSTANCE.moduleManager.getModules()) {
                if (module.getName().equalsIgnoreCase(args.get(0))) {
                    if (args.get(0).isEmpty()) {
                        ClientMessage.sendMessage("Please Only Enter One Character");
                        return;
                    }
                    String bind = args.get(1);
                    int key = Keyboard.getKeyIndex(bind.toUpperCase());
                    if (key == 0) {
                        ClientMessage.sendMessage("Unknown Keybind");
                        return;
                    }
                    module.setKeybind(key);
                    ClientMessage.sendMessage(module.getName() + " Has Been Bound To " + Keyboard.getKeyName(module.getKeybind()));
                }
            }
        } else {
            ClientMessage.sendErrorMessage(getSyntax());
        }
    }
}
