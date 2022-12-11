package org.hockey.hockeyware.client.events.network;


import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateTimerEvent extends Event {
    public float timerSpeed;

    public UpdateTimerEvent(float timerSpeed) {
        this.timerSpeed = timerSpeed;
    }
}