package org.hockey.hockeyware.client.util.math;

public class Rotation{

    private float yaw, pitch;

    private final Rotate rotate;

    public Rotation(float yaw, float pitch, Rotate rotate) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = rotate;
    }

    public Rotation(float yaw, float pitch) {
        this(yaw, pitch, Rotate.NONE);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float in) {
        yaw = in;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float in) {
        pitch = in;
    }

    public Rotate getRotation() {
        return rotate;
    }

    public boolean isValid() {
        return !Float.isNaN(getYaw()) && !Float.isNaN(getPitch());
    }

    public enum Rotate {

        PACKET,

        CLIENT,

        NONE
    }
}