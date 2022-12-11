package org.hockey.hockeyware.client.features.command.commands;

import net.minecraft.util.text.TextFormatting;
import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.util.client.ClientMessage;

import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows All The HockeyWare Commands", "help");
    }

    @Override
    public void runCommand(List<String> args) {
        try {
            for (Command command : getHockey().commandManager.getCommands()) {
                ClientMessage.sendMessage(TextFormatting.WHITE + command.getName() + TextFormatting.GRAY + " " + command.getDescription() + " syntax: " + command.getSyntax());
            }
        } catch (Exception ignored) {
        }
    }
}
