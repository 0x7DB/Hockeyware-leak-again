package org.hockey.hockeyware.client.events.player;

import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ItemInputUpdateEvent extends Event {

    private final MovementInput movementInput;

    public ItemInputUpdateEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput() {
        return movementInput;
    }
}