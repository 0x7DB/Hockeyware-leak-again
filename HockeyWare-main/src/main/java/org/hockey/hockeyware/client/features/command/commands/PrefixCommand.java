package org.hockey.hockeyware.client.features.command.commands;

import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.manager.CommandManager;
import org.hockey.hockeyware.client.util.client.ClientMessage;

import java.util.List;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix", "Allows You To Change The HockeyWare Chat Prefix", "prefix [prefix]");
    }

    @Override
    public void runCommand(List<String> args) {
        if (args.size() >= 1) {
            CommandManager.setPrefix(args.get(0));
            ClientMessage.sendMessage("Set The HockeyWare Chat Prefix To " + args.get(0));
        } else {
            ClientMessage.sendErrorMessage(getSyntax());
        }
    }
}
