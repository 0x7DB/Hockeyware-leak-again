package org.hockey.hockeyware.client.manager.friend;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.hockey.hockeyware.client.events.client.FriendEvent;

import java.util.ArrayList;
import java.util.UUID;

public class FriendManager {
    EntityPlayer friendedPlayer;
    UUID frienduuid;
    private static ArrayList<Friend> friends = new ArrayList<>();

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friendsToAdd) {
        friends = friendsToAdd;
    }

    public static void add(Friend friend) {
        friends.add(friend);
        MinecraftForge.EVENT_BUS.post(new FriendEvent(friend.getAlias(), friend.getUuid(), FriendEvent.Type.Add));
    }

    public static void remove(Friend friend) {
        friends.remove(friend);
        MinecraftForge.EVENT_BUS.post(new FriendEvent(friend.getAlias(), friend.getUuid(), FriendEvent.Type.Remove));
    }

    public static Friend getFriend(UUID uuid) {
        return friends.stream().filter((friend) -> friend.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public EntityPlayer getPlayer() {
        for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
            if (player.getUniqueID().equals(getAllFriends())) {
                friendedPlayer = player;
            }
        }
        return friendedPlayer;
    }

    public UUID getAllFriends() {
        for (Friend friend : getFriends()) {
            frienduuid = friend.getUuid();
        }
        return frienduuid;
    }

    public boolean isFriendByName(String alias) {
        for (Friend friend : getFriends()) {
            if (friend.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
    }

    public Friend getFriendByName(String name) {
        Friend friend1 = null;
        for (Friend friend : getFriends()) {
            if (friend.getAlias().equals(name)) {
                friend1 = friend;
            }
        }
        return friend1;
    }

    public boolean isFriend(UUID uuid) {
        return friends.stream().anyMatch((friend) -> friend.getUuid().equals(uuid));
    }


    public void addNoEvent(Friend friend) {
        friends.add(friend);
    }
}
