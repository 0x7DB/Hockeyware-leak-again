package org.hockey.hockeyware.client.events.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class UpdateWalkingPlayerEvent extends Event {
    private final UpdateWalkingPlayerEvent.Era era;

    private double x, y, z;
    private boolean onGround;
    private float yaw, pitch;
    private int iterations;

    public UpdateWalkingPlayerEvent() {
        this.era = UpdateWalkingPlayerEvent.Era.POST;
    }

    public UpdateWalkingPlayerEvent(double x, double y, double z, boolean onGround, float yaw, float pitch) {
        this.era = UpdateWalkingPlayerEvent.Era.PRE;

        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    public UpdateWalkingPlayerEvent.Era getEra() {
        return era;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Sets the number of iterations
     * @param in The new number of iterations
     */
    public void setIterations(int in) {
        iterations = in;
    }

    /**
     * Gets the iterations
     * @return The number of iterations
     */
    public int getIterations() {
        return iterations;
    }

    public enum Era {
        PRE, POST
    }
}
