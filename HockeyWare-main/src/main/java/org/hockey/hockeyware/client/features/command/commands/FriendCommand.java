package org.hockey.hockeyware.client.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.manager.friend.Friend;
import org.hockey.hockeyware.client.util.client.ClientMessage;

import java.util.List;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "Add/Remove/List HockeyWare Friends", "friend" + " " + "[add/remove/list]" + " " + "[playername]");
    }

    @Override
    public void runCommand(List<String> args) {
        if (args.size() >= 1) {
            if (args.size() >= 2) {
                try {
                    if (args.get(0).equalsIgnoreCase("add") && !getHockey().friendManager.isFriendByName(args.get(1))) {
                        if (!HockeyWare.INSTANCE.friendManager.isFriendByName(args.get(1))) {
                            HockeyWare.INSTANCE.friendManager.add(new Friend(args.get(1)));
                            ClientMessage.sendMessage(args.get(1) + " Has Been " + ChatFormatting.GREEN + "Friended");
                        } else {
                            ClientMessage.sendMessage(args.get(1) + " Is Already A Friend");
                        }
                    }
                    if (args.get(0).equalsIgnoreCase("remove") && getHockey().friendManager.isFriendByName(args.get(1))) {
                        if (HockeyWare.INSTANCE.friendManager.isFriendByName(args.get(1))) {
                            Friend friend = getHockey().friendManager.getFriendByName(args.get(1));
                            HockeyWare.INSTANCE.friendManager.remove(friend);
                            ClientMessage.sendMessage(args.get(1) + " Has Been " + ChatFormatting.RED + "Unfriended");
                        } else {
                            ClientMessage.sendMessage(args.get(1) + " Is Not A Friend");
                        }
                    }

                } catch (NullPointerException ignored) {
                }

            }
            if (args.get(0).equalsIgnoreCase("list")) {
                if (HockeyWare.INSTANCE.friendManager.getFriends().isEmpty()) {
                    ClientMessage.sendMessage("You Have No Friends");
                } else {
                    for (Friend friend : HockeyWare.INSTANCE.friendManager.getFriends()) {
                        ClientMessage.sendMessage(friend.getAlias());
                    }
                }
            }
        } else {
            ClientMessage.sendErrorMessage(getSyntax());
        }
    }
}
