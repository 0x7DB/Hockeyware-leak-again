package org.hockey.hockeyware.client.events.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class ConnectionEvent extends Event {
    final UUID uuid;
    final EntityPlayer entity;
    final String name;
    final Type type;

    public ConnectionEvent(UUID uuid, String name, Type type) {
        this.uuid = uuid;
        this.name = name;
        this.entity = null;
        this.type = type;
    }

    public ConnectionEvent(EntityPlayer entity, UUID uuid, String name, Type type) {
        this.entity = entity;
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        Join, Leave, Other
    }
}
