package org.hockey.hockeyware.client.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static org.hockey.hockeyware.client.util.Globals.mc;

public class AngleUtil{

    /**
     * Calculates the rotations (yaw & pitch) to a vector
     * @param to The vector to find rotations to
     * @return The rotations to the vector
     */
    public static org.hockey.hockeyware.client.util.player.Rotation calculateAngles(Vec3d to) {

        // find the yaw and pitch to the vector
        float yaw = (float) (Math.toDegrees(Math.atan2(to.subtract(mc.player.getPositionEyes(1)).z, to.subtract(mc.player.getPositionEyes(1)).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(to.subtract(mc.player.getPositionEyes(1)).y, Math.hypot(to.subtract(mc.player.getPositionEyes(1)).x, to.subtract(mc.player.getPositionEyes(1)).z)));

        // wrap the degrees to values between -180 and 180
        return new org.hockey.hockeyware.client.util.player.Rotation(MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch));
    }

    /**
     * Gets the vector for a specified rotation
     * @param rotation The rotation
     * @return The vector for the specified rotation
     */
    public static Vec3d getVectorForRotation(Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
}