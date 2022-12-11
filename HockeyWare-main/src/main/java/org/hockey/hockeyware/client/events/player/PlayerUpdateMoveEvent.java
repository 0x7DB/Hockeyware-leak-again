package org.hockey.hockeyware.client.events.player;

import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerUpdateMoveEvent extends Event {
    public MovementInput movementInput;

    public PlayerUpdateMoveEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}