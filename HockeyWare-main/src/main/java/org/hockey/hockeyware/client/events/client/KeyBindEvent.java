package org.hockey.hockeyware.client.events.client;

import net.minecraftforge.fml.common.eventhandler.Event;

public class KeyBindEvent extends Event {

    public boolean holding;
    public boolean pressed;

    public KeyBindEvent(boolean holding, boolean pressed) {
        super();
        this.holding = holding;
        this.pressed = pressed;
    }

    public boolean isHolding() {
        return holding;
    }

    public boolean isPressed() {
        return pressed;
    }
}