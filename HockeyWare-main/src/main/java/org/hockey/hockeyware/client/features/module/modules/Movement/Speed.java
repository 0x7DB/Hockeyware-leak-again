package org.hockey.hockeyware.client.features.module.modules.Movement;

import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.events.player.MotionEvent;
import org.hockey.hockeyware.client.events.player.RotationUpdateEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.mixin.mixins.accessor.ICPacketPlayer;
import org.hockey.hockeyware.client.mixin.mixins.accessor.IEntity;
import org.hockey.hockeyware.client.mixin.mixins.accessor.IEntityPlayerSP;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.player.MotionUtil;
import org.hockey.hockeyware.client.util.player.PlayerUtil;

public class Speed extends Module {
    public static Speed INSTANCE;

    public Speed() {
        super("Speed", "Allows You To Go Faster",  Category.Movement);
        INSTANCE = this;
    }


    public static Setting<Mode> mode = new Setting<>("Mode", Mode.StrafeStrict);

    public static Setting<BaseSpeed> speed = new Setting<>("Speed", BaseSpeed.NORMAL);
    public static Setting<Friction> friction = new Setting<>("Friction", Friction.CUTOFF);
    public static Setting<Boolean> potionFactor = new Setting<>("PotionFactor", false);
    public static Setting<Boolean> strictJump = new Setting<>("StrictJump", true);
    public static Setting<Boolean> strictSprint = new Setting<>("StrictSprint", false);

    public static Setting<Boolean> timer = new Setting<>("Timer", false);

    private int strafeStage = 4;

    private int groundStage = 2;

    private double moveSpeed;
    private double latestMoveSpeed;

    private double boostSpeed;

    private boolean accelerate;

    private int strictTicks;

    private int boostTicks;

    private boolean offsetPackets;

    @Override
    public void onEnable() {
        super.onEnable();

        // awesome
//        strafeStage = 4;
//        groundStage = 2;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        resetProcess();
    }

    @SubscribeEvent
    public void onRotationUpdate(RotationUpdateEvent event) {
        latestMoveSpeed = Math.sqrt(StrictMath.pow(mc.player.posX - mc.player.prevPosX, 2) + StrictMath.pow(mc.player.posZ - mc.player.prevPosZ, 2));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public void onMotion(MotionEvent event) {

        if (friction.getValue().equals(Friction.CUTOFF)) {

            if (PlayerUtil.isInLiquid()) {
                resetProcess();
                return;
            }

            if (((IEntity) mc.player).getInWeb()) {
                resetProcess();
                return;
            }
        }

        if (mc.player.isOnLadder() || mc.player.capabilities.isFlying || mc.player.isElytraFlying() || mc.player.fallDistance > 2) {
            resetProcess();
            return;
        }

        if (mc.player.isSneaking()) {
            return;
        }

        event.setCanceled(true);
        getHockey().getTickManager.setClientTicks(1);

        double baseSpeed = 0.2873;

        if (speed.getValue().equals(BaseSpeed.OLD)) {
            baseSpeed = 0.272;
        }

        if (potionFactor.getValue()) {
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                double amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                baseSpeed *= 1 + (0.2 * (amplifier + 1));
            }

            if (mc.player.isPotionActive(MobEffects.SLOWNESS)) {
                double amplifier = mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier();
                baseSpeed /= 1 + (0.2 * (amplifier + 1));
            }
        }

        if (strictSprint.getValue() && (!mc.player.isSprinting() || !((IEntityPlayerSP) mc.player).getServerSprintState())) {
            if (mc.getConnection() != null) {
                mc.getConnection().getNetworkManager().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
        }

        switch (mode.getValue()) {

            case OnGround: {

                if (mc.player.onGround && MotionUtil.isMoving()) {

                    if (groundStage == 2) {

                        offsetPackets = true;

                        double acceleration = 2.149;

                        moveSpeed *= acceleration;

                        groundStage = 3;
                    }

                    else if (groundStage == 3) {

                        double scaledMoveSpeed = 0.66 * (latestMoveSpeed - baseSpeed);

                        moveSpeed = latestMoveSpeed - scaledMoveSpeed;

                        groundStage = 2;
                    }

                    if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 0.21, 0)).size() > 0 || mc.player.collidedVertically) {
                        groundStage = 1;
                    }
                }

                moveSpeed = Math.max(moveSpeed, baseSpeed);

                float forward = mc.player.movementInput.moveForward;
                float strafe = mc.player.movementInput.moveStrafe;
                float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

                if (!MotionUtil.isMoving()) {
                    event.setX(0);
                    event.setZ(0);
                }

                else if (forward != 0) {
                    if (strafe > 0) {
                        yaw += forward > 0 ? -45 : 45;
                    }

                    else if (strafe < 0) {
                        yaw += forward > 0 ? 45 : -45;
                    }

                    strafe = 0;

                    if (forward > 0) {
                        forward = 1;
                    }

                    else if (forward < 0) {
                        forward = -1;
                    }
                }

                double cos = Math.cos(Math.toRadians(yaw));
                double sin = -Math.sin(Math.toRadians(yaw));

                // update the movements
                event.setX((forward * moveSpeed * sin) + (strafe * moveSpeed * cos));
                event.setZ((forward * moveSpeed * cos) - (strafe * moveSpeed * sin));
                break;
            }

            case Strafe: {

                if (MotionUtil.isMoving()) {

                    if (timer.getValue()) {
                        getHockey().getTickManager.setClientTicks(1.088F);
                    }

                    if (strafeStage == 1) {

                        moveSpeed = 1.35 * baseSpeed - 0.01;
                    }

                    else if (strafeStage == 2) {

                        double jumpSpeed = 0.3999999463558197;

                        if (potionFactor.getValue()) {

                            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                                double amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
                                jumpSpeed += (amplifier + 1) * 0.1;
                            }
                        }

                        mc.player.motionY = jumpSpeed;
                        event.setY(jumpSpeed);

                        double acceleration = 1.395;

                        if (accelerate) {
                            acceleration = 1.6835;
                        }

                        moveSpeed *= acceleration;
                    }

                    else if (strafeStage == 3) {

                        double scaledMoveSpeed = 0.66 * (latestMoveSpeed - baseSpeed);

                        // scale the move speed
                        moveSpeed = latestMoveSpeed - scaledMoveSpeed;

                        // we've just slowed down and need to alternate acceleration
                        accelerate = !accelerate;
                    }

                    else {
                        if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, mc.player.motionY, 0)).size() > 0 || mc.player.collidedVertically) && strafeStage > 0) {

                            // reset strafe stage
                            strafeStage = MotionUtil.isMoving() ? 1 : 0;
                        }

                        // collision speed
                        moveSpeed = latestMoveSpeed - (latestMoveSpeed / 159);
                    }

                    // do not allow movements slower than base speed
                    moveSpeed = Math.max(moveSpeed, baseSpeed);

                    // the current movement input values of the user
                    float forward = mc.player.movementInput.moveForward;
                    float strafe = mc.player.movementInput.moveStrafe;
                    float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

                    // if we're not inputting any movements, then we shouldn't be adding any motion
                    if (!MotionUtil.isMoving()) {
                        event.setX(0);
                        event.setZ(0);
                    }

                    else if (forward != 0) {
                        if (strafe > 0) {
                            yaw += forward > 0 ? -45 : 45;
                        }

                        else if (strafe < 0) {
                            yaw += forward > 0 ? 45 : -45;
                        }

                        strafe = 0;

                        if (forward > 0) {
                            forward = 1;
                        }

                        else if (forward < 0) {
                            forward = -1;
                        }
                    }

                    // our facing values, according to movement not rotations
                    double cos = Math.cos(Math.toRadians(yaw));
                    double sin = -Math.sin(Math.toRadians(yaw));

                    // update the movements
                    event.setX((forward * moveSpeed * sin) + (strafe * moveSpeed * cos));
                    event.setZ((forward * moveSpeed * cos) - (strafe * moveSpeed * sin));

                    // update
                    strafeStage++;
                }

                break;
            }

            /*
             * Mode: Strafe for NCP Updated
             * Max speed: ~26 or 27 kmh
             */
            case StrafeStrict: {

                // only attempt to modify speed if we are inputting movement
                if (MotionUtil.isMoving()) {

                    // use timer
                    if (timer.getValue()) {
                        getHockey().getTickManager.setClientTicks(1.088F);
                    }

                    // start the motion
                    if (strafeStage == 1) {

                        // starting speed
                        moveSpeed = 1.35 * baseSpeed - 0.01;
                    }

                    // start jumping
                    else if (strafeStage == 2) {

                        // the jump height
                        double jumpSpeed = 0.3999999463558197;

                        // jump slightly higher (i.e. slower, this uses vanilla jump height)
                        if (strictJump.getValue()) {
                            jumpSpeed = 0.41999998688697815;
                        }

                        // scale jump speed if Jump Boost potion effect is active
                        if (potionFactor.getValue()) {

                            // not really too useful for Speed like the other potion effects
                            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                                double amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
                                jumpSpeed += (amplifier + 1) * 0.1;
                            }
                        }

                        // jump
                        mc.player.motionY = jumpSpeed;
                        event.setY(jumpSpeed);

                        // acceleration jump factor
                        double acceleration = 2.149;

                        // since we just jumped, we can now move faster
                        moveSpeed *= acceleration;
                    }

                    // start actually speeding when falling
                    else if (strafeStage == 3) {

                        // take into account our last tick's move speed
                        double scaledMoveSpeed = 0.66 * (latestMoveSpeed - baseSpeed);

                        // scale the move speed
                        moveSpeed = latestMoveSpeed - scaledMoveSpeed;
                    }

                    else {
                        if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, mc.player.motionY, 0)).size() > 0 || mc.player.collidedVertically) && strafeStage > 0) {

                            // reset strafe stage
                            strafeStage = MotionUtil.isMoving() ? 1 : 0;
                        }

                        // collision speed
                        moveSpeed = latestMoveSpeed - (latestMoveSpeed / 159);
                    }

                    // do not allow movements slower than base speed
                    moveSpeed = Math.max(moveSpeed, baseSpeed);

                    // base speeds
                    double baseStrictSpeed = 0.465;
                    double baseRestrictedSpeed = 0.44;

                    // scale move speed if Speed or Slowness potion effect is active
                    if (potionFactor.getValue()) {
                        if (mc.player.isPotionActive(MobEffects.SPEED)) {
                            double amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                            baseStrictSpeed *= 1 + (0.2 * (amplifier + 1));
                            baseRestrictedSpeed *= 1 + (0.2 * (amplifier + 1));
                        }

                        if (mc.player.isPotionActive(MobEffects.SLOWNESS)) {
                            double amplifier = mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier();
                            baseStrictSpeed /= 1 + (0.2 * (amplifier + 1));
                            baseRestrictedSpeed /= 1 + (0.2 * (amplifier + 1));
                        }
                    }

                    // clamp the value based on the number of ticks passed
                    moveSpeed = Math.min(moveSpeed, strictTicks > 25 ? baseStrictSpeed : baseRestrictedSpeed);

                    // update & reset our tick count
                    strictTicks++;

                    // reset strict ticks every 50 ticks
                    if (strictTicks > 50) {
                        strictTicks = 0;
                    }

                    // the current movement input values of the user
                    float forward = mc.player.movementInput.moveForward;
                    float strafe = mc.player.movementInput.moveStrafe;
                    float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

                    // if we're not inputting any movements, then we shouldn't be adding any motion
                    if (!MotionUtil.isMoving()) {
                        event.setX(0);
                        event.setZ(0);
                    }

                    else if (forward != 0) {
                        if (strafe >= 1) {
                            yaw += (forward > 0 ? -45 : 45);
                            strafe = 0;
                        }

                        else if (strafe <= -1) {
                            yaw += (forward > 0 ? 45 : -45);
                            strafe = 0;
                        }

                        if (forward > 0) {
                            forward = 1;
                        }

                        else if (forward < 0) {
                            forward = -1;
                        }
                    }

                    // our facing values, according to movement not rotations
                    double cos = Math.cos(Math.toRadians(yaw));
                    double sin = -Math.sin(Math.toRadians(yaw));

                    // update the movements
                    event.setX((forward * moveSpeed * sin) + (strafe * moveSpeed * cos));
                    event.setZ((forward * moveSpeed * cos) - (strafe * moveSpeed * sin));

                    // update
                    strafeStage++;
                }

                break;
            }

            /*
             * Maintains speed at 22.4 kmh on ground
             * Similar to Sprint
             */
            case StrafeGround: {

                // instant max speed
                moveSpeed = baseSpeed;

                // walking speed = 0.7692307692 * sprint speed
                if (!mc.player.isSprinting()) {
                    moveSpeed *= 0.7692307692;
                }

                // sneak scale = 0.3 * sprint speed
                else if (mc.player.isSneaking()) {
                    moveSpeed *= 0.3;
                }

                // the current movement input values of the user
                float forward = mc.player.movementInput.moveForward;
                float strafe = mc.player.movementInput.moveStrafe;
                float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

                // if we're not inputting any movements, then we shouldn't be adding any motion
                if (!MotionUtil.isMoving()) {
                    event.setX(0);
                    event.setZ(0);
                }

                if (forward != 0) {
                    if (strafe > 0) {
                        yaw += ((forward > 0) ? -45 : 45);
                    }

                    else if (strafe < 0) {
                        yaw += ((forward > 0) ? 45 : -45);
                    }

                    strafe = 0;
                    if (forward > 0) {
                        forward = 1;
                    }

                    else if (forward < 0) {
                        forward = -1;
                    }
                }

                // our facing values, according to movement not rotations
                double cos = Math.cos(Math.toRadians(yaw));
                double sin = -Math.sin(Math.toRadians(yaw));

                // update the movements
                event.setX((forward * moveSpeed * sin) + (strafe * moveSpeed * cos));
                event.setZ((forward * moveSpeed * cos) - (strafe * moveSpeed * sin));
                break;
            }

            /*
             * Similar to Mode: Strafe with a lower jump height in order to reach higher speeds
             * Max speed: ~31 kmh
             */
            case StrafeLow:
                break;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketEntityAction) {

            // slowdown movement
            if (((CPacketEntityAction) event.getPacket()).getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING) || ((CPacketEntityAction) event.getPacket()).getAction().equals(CPacketEntityAction.Action.START_SNEAKING)) {

                // keep sprint
                if (strictSprint.getValue()) {
                    event.setCanceled(true);
                }
            }
        }

        if (event.getPacket() instanceof CPacketPlayer) {
            if (((ICPacketPlayer) event.getPacket()).isMoving() && offsetPackets) {

                // offset packets
                ((ICPacketPlayer) event.getPacket()).setY(((CPacketPlayer) event.getPacket()).getY(0) + (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 0.21, 0)).size() > 0 ? 2 : 4));
                offsetPackets = false;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {

        // reset our process on a rubberband
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            resetProcess();
        }

        // boost our speed when taking explosion damage
        if (event.getPacket() instanceof SPacketExplosion) {

            // velocity from explosion
            double boostMotionX = StrictMath.pow(((SPacketExplosion) event.getPacket()).getMotionX() / 8000F, 2);
            double boostMotionZ = StrictMath.pow(((SPacketExplosion) event.getPacket()).getMotionX() / 8000F, 2);

            // boost our speed
            boostSpeed = Math.sqrt(boostMotionX + boostMotionZ);

            // start our timer
            boostTicks = 0;
        }

        // boost our speed when taking knockback damage
        if (event.getPacket() instanceof SPacketEntityVelocity) {

            // check if velocity is applied to player
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {

                // velocity from knockback
                double boostMotionX = StrictMath.pow(((SPacketEntityVelocity) event.getPacket()).getMotionX() / 8000F, 2);
                double boostMotionZ = StrictMath.pow(((SPacketEntityVelocity) event.getPacket()).getMotionX() / 8000F, 2);

                // boost our speed
                boostSpeed = Math.sqrt(boostMotionX + boostMotionZ);

                // start our timer
                boostTicks = 0;
            }
        }
    }

    /**
     * Resets the Speed process and sets all values back to defaults
     */
    public void resetProcess() {
        strafeStage = 4;
        groundStage = 2;
        moveSpeed = 0;
        latestMoveSpeed = 0;
        boostSpeed = 0;
        strictTicks = 0;
        boostTicks = 0;
        accelerate = false;
        offsetPackets = false;
    }

    public enum Mode {

        /**
         * Speed that automatically jumps to simulate BHops
         */
        Strafe,

        StrafeStrict,

        /**
         * Strafe with a lower jump height
         */
        StrafeLow,

        /**
         * Speeds your movement while on the ground
         */
        StrafeGround,

        /**
         * Speeds your movement while on the ground, spoofs jump state
         */
        OnGround
    }

    public enum BaseSpeed {

        /**
         * Base speed for NCP
         */
        NORMAL,

        /**
         * Base speed for old NCP
         */
        OLD
    }

    public enum Friction {

        /**
         * Factors in material friction but otherwise retains all functionality
         */
        FACTOR,

        /**
         * Ignores friction
         */
        FAST,

        /**
         * Stop all speed when experiencing friction
         */
        CUTOFF
    }
}