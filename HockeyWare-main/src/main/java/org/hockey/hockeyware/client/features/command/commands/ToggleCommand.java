package org.hockey.hockeyware.client.features.command.commands;


import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.util.client.ClientMessage;

import java.util.List;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("toggle", "Allows you to toggle modules", "toggle" + " " + "[module]");
    }

    @Override
    public void runCommand(List<String> args) {
        if (args.size() >= 1) {
            for (Module module : getHockey().moduleManager.getModules()) {
                if (module.getName().equalsIgnoreCase(args.get(0))) {
                    module.toggle(false);
                }
            }
        } else {
            ClientMessage.sendErrorMessage(getSyntax());
        }
    }
}