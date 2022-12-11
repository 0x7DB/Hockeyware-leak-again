package org.hockey.hockeyware.client.events.render;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderEntityEvent extends Event {
    public final Entity entity;
    public final Type type;

    public RenderEntityEvent(Entity e, Type t) {
        entity = e;
        type = t;
    }

    public enum Type {
        TEXTURE, COLOR
    }

    public Entity getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }

    public static class Head extends RenderEntityEvent {
        public Head(Entity e, Type t) {
            super(e, t);
        }
    }

    public static class Return extends RenderEntityEvent {
        public Return(Entity e, Type t) {
            super(e, t);
        }
    }
}