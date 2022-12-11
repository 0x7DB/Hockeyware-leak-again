package org.hockey.hockeyware.client.manager;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.events.player.MotionUpdateEvent;
import org.hockey.hockeyware.client.events.player.RenderRotationsEvent;
import org.hockey.hockeyware.client.mixin.mixins.accessor.ICPacketPlayer;
import org.hockey.hockeyware.client.util.Globals;
import org.hockey.hockeyware.client.util.player.Rotation;

public class RotationManager implements Globals {

    // the current server rotation
    private final Rotation serverRotation = new Rotation(Float.NaN, Float.NaN);

    // current rotation
    private Rotation rotation = new Rotation(Float.NaN, Float.NaN);
    private long stay = 0L;

    public RotationManager() {
        HockeyWare.EVENT_BUS.register(this);
    }

    public void on() {
        if (System.currentTimeMillis() - stay >= 250L && rotation.isValid()) {
            rotation = new Rotation(Float.NaN, Float.NaN);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {

        // rotation packet
        if (event.getPacket() instanceof CPacketPlayer) {

            // packet
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            // check if the packet has rotations
            if (((ICPacketPlayer) packet).isRotating()) {

                // update our server rotation
                serverRotation.setYaw(packet.getYaw(0));
                serverRotation.setPitch(packet.getPitch(0));
            }
        }
    }


    @SubscribeEvent
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (rotation.isValid()) {

            // cancel, we'll send our own rotations
            event.setCanceled(true);

            // assign vanilla values
            event.setOnGround(mc.player.onGround);
            event.setX(mc.player.posX);
            event.setY(mc.player.getEntityBoundingBox().minY);
            event.setZ(mc.player.posZ);

            // set the rotation to be our custom value
            event.setYaw(rotation.getYaw());
            event.setPitch(rotation.getPitch());
        }
    }

    @SubscribeEvent
    public void onRenderRotations(RenderRotationsEvent event) {

        // we only want to force rotation rendering if we are currently rotating
        if (rotation.isValid()) {

            // cancel, we'll render our own rotations
            event.setCanceled(true);

            // we should render rotations on the server rotation rather than our client side rotations
            // as the two could not match
            event.setYaw(serverRotation.getYaw());
            event.setPitch(serverRotation.getPitch());
        }
    }

    /**
     * Queues a rotation to be sent on the next tick
     * @param in The rotation to be sent on the next tick
     */
    public void setRotation(Rotation in) {
        rotation = in;
        stay = System.currentTimeMillis();
    }

    /**
     * Gets the current client rotations
     * @return The current client rotations
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Gets the current server rotations
     * @return The current server rotations
     */
    public Rotation getServerRotation() {
        return serverRotation;
    }
}