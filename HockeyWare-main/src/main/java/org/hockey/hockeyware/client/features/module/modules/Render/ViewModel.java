package org.hockey.hockeyware.client.features.module.modules.Render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.player.RenderHeldItemEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.Timer;

public class ViewModel extends Module {

    //public static final Setting<Boolean> itemFOV = new Setting<>("Item FOV", false);
    //public static final Setting<Integer> fov = new Setting<>("FOV", 90, 0, 180);

//    private static final Setting<Boolean> affectsHand = new Setting<>("Affects Hand", true);

    private static final Setting<Boolean> centeredEat = new Setting<>("Centered Eat", true);

//    private final Setting<Boolean> noHandRotate = new Setting<>("Rotation Settings Effect Hand", false);

    public static final Setting<Boolean> rightYawSpin = new Setting<>("Right Yaw Spin", false);
    public static final Setting<Boolean> rightPitchSpin = new Setting<>("Right Pitch Spin", false);
    public static final Setting<Boolean> rightRollSpin = new Setting<>("Right Roll Spin", false);
    public static final Setting<Float> rightYawSpeed = new Setting<>("Right Yaw Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Float> rightPitchSpeed = new Setting<>("Right Pitch Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Float> rightRollSpeed = new Setting<>("Right Roll Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Double> rightX = new Setting<>("Right X", 0.0, -2.0, 2.0);
    public static final Setting<Double> rightY = new Setting<>("Right Y", 0.0, -2.0, 2.0);
    public static final Setting<Double> rightZ = new Setting<>("Right Z", 0.0, -2.0, 2.0);
    public static final Setting<Float> rightYaw = new Setting<>("Right Yaw", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> rightPitch = new Setting<>("Right Pitch", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> rightRoll = new Setting<>("Right Roll", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> rightScaleX = new Setting<>("Right Scale X", 1.0f, 0.0f, 2.0f);
    public static final Setting<Float> rightScaleY = new Setting<>("Right Scale Y", 1.0f, 0.0f, 2.0f);
    public static final Setting<Float> rightScaleZ = new Setting<>("Right Scale Z", 1.0f, 0.0f, 2.0f);

    public static final Setting<Boolean> leftYawSpin = new Setting<>("Left Yaw Spin", false);
    public static final Setting<Boolean> leftPitchSpin = new Setting<>("Left Pitch Spin", false);
    public static final Setting<Boolean> leftRollSpin = new Setting<>("Left Roll Spin", false);
    public static final Setting<Float> leftYawSpeed = new Setting<>("Left Yaw Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Float> leftPitchSpeed = new Setting<>("Left Pitch Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Float> leftRollSpeed = new Setting<>("Left Roll Speed", 0.0f, -15.0f, 15.0f);
    public static final Setting<Double> leftX = new Setting<>("Left X", 0.0, -2.0, 2.0);
    public static final Setting<Double> leftY = new Setting<>("Left Y", 0.0, -2.0, 2.0);
    public static final Setting<Double> leftZ = new Setting<>("Left Z", 0.0, -2.0, 2.0);
    public static final Setting<Float> leftYaw = new Setting<>("Left Yaw", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> leftPitch = new Setting<>("Left Pitch", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> leftRoll = new Setting<>("Left Roll", 0.0f, -180.0f, 180.0f);
    public static final Setting<Float> leftScaleX = new Setting<>("Left Scale X", 1.0f, 0.0f, 2.0f);
    public static final Setting<Float> leftScaleY = new Setting<>("Left Scale Y", 1.0f, 0.0f, 2.0f);
    public static final Setting<Float> leftScaleZ = new Setting<>("Left Scale Z", 1.0f, 0.0f, 2.0f);

    public static ViewModel INSTANCE;

    public ViewModel() {
        super("ViewModel", "Allows You To Change The Position And Scale Of Your Held Items", Category.Render);
        INSTANCE = this;
    }

    private final Timer timer = new Timer();

//    @SubscribeEvent
//    public void renderItemFOV(EntityViewRenderEvent.FOVModifier event) {
//        if (itemFOV.getValue())
//            event.setFOV(fov.getValue());
//    }

    @SubscribeEvent
    public void onRenderHeldItemPreEventListener(RenderHeldItemEvent.Pre event) {
//        if(affectsHand.getValue())
//            return;
        switch (event.getSide()) {
            case LEFT: {
                if(!centeredEat.getValue() || !isPlayerEatingAndNotNull())
                    GlStateManager.translate(leftX.getValue() / 2, leftY.getValue() / 2, leftZ.getValue() / 2);
                GlStateManager.scale(leftScaleX.getValue(), leftScaleY.getValue(), leftScaleZ.getValue());
                break;
            }
            case RIGHT: {
                if(!centeredEat.getValue() || !isPlayerEatingAndNotNull())
                    GlStateManager.translate(rightX.getValue() / 2, rightY.getValue() / 2, rightZ.getValue() / 2);
                GlStateManager.scale(rightScaleX.getValue(), rightScaleY.getValue(), rightScaleZ.getValue());
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderHeldItemPostEventListener(RenderHeldItemEvent.Post event) {
//        if(affectsHand.getValue())
//            return;
        switch (event.getSide()) {
            case LEFT: {
                if(centeredEat.getValue() && isPlayerEatingAndNotNull())
                    break;
                GlStateManager.rotate(leftYaw.getValue() + (leftYawSpin.getValue() ? (timer.getTimePassed() / 100f) * leftYawSpeed.getValue() : 0f), 0, 1, 0);
                GlStateManager.rotate(leftPitch.getValue() + (leftPitchSpin.getValue() ? (timer.getTimePassed() / 100f) * leftPitchSpeed.getValue() : 0f), 1, 0, 0);
                GlStateManager.rotate(leftRoll.getValue() + (leftRollSpin.getValue() ? (timer.getTimePassed() / 100f) * leftRollSpeed.getValue() : 0f), 0, 0, 1);
                break;
            }
            case RIGHT: {
                if(centeredEat.getValue() && isPlayerEatingAndNotNull())
                    break;
                GlStateManager.rotate(rightYaw.getValue() + (rightYawSpin.getValue() ? (timer.getTimePassed() / 100f) * rightYawSpeed.getValue() : 0f), 0, 1, 0);
                GlStateManager.rotate(rightPitch.getValue() + (rightPitchSpin.getValue() ? (timer.getTimePassed() / 100f) * rightPitchSpeed.getValue() : 0f), 1, 0, 0);
                GlStateManager.rotate(rightRoll.getValue() + (rightRollSpin.getValue() ? (timer.getTimePassed() / 100f) * rightRollSpeed.getValue() : 0f), 0, 0, 1);
                break;
            }
        }
    }

    public void hand(EnumHandSide side){
        if(side == EnumHandSide.RIGHT){
            if(!centeredEat.getValue() || !isPlayerEatingAndNotNull())
                GlStateManager.translate(rightX.getValue() / 2, rightY.getValue() / 2, rightZ.getValue() / 2);
            if((!centeredEat.getValue() || !isPlayerEatingAndNotNull())){
//          if(!noHandRotate.getValue() && (!centeredEat.getValue() || !isPlayerEatingAndNotNull())){
                GlStateManager.rotate(rightYaw.getValue() + (rightYawSpin.getValue() ? (timer.getTimePassed() / 100f) * rightYawSpeed.getValue() : 0f), 0, 1, 0);
                GlStateManager.rotate(rightPitch.getValue() + (rightPitchSpin.getValue() ? (timer.getTimePassed() / 100f) * rightPitchSpeed.getValue() : 0f), 1, 0, 0);
                GlStateManager.rotate(rightRoll.getValue() + (rightRollSpin.getValue() ? (timer.getTimePassed() / 100f) * rightRollSpeed.getValue() : 0f), 0, 0, 1);
            }
            GlStateManager.scale(rightScaleX.getValue(), rightScaleY.getValue(), rightScaleZ.getValue());
            return;
        }
        if(!centeredEat.getValue() || !isPlayerEatingAndNotNull())
            GlStateManager.translate(leftX.getValue() / 2, leftY.getValue() / 2, leftZ.getValue() / 2);
        if(!centeredEat.getValue() || !isPlayerEatingAndNotNull()){
            GlStateManager.rotate(leftYaw.getValue() + (leftYawSpin.getValue() ? (timer.getTimePassed() / 100f) * leftYawSpeed.getValue() : 0f), 0, 1, 0);
            GlStateManager.rotate(leftPitch.getValue() + (leftPitchSpin.getValue() ? (timer.getTimePassed() / 100f) * leftPitchSpeed.getValue() : 0f), 1, 0, 0);
            GlStateManager.rotate(leftRoll.getValue() + (leftRollSpin.getValue() ? (timer.getTimePassed() / 100f) * leftRollSpeed.getValue() : 0f), 0, 0, 1);
        }
        GlStateManager.scale(leftScaleX.getValue(), leftScaleY.getValue(), leftScaleZ.getValue());
    }

    private boolean isPlayerEatingAndNotNull(){
        return mc.player != null
                && mc.world != null
                && mc.player.isHandActive()
                && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood);
    }

//    public static boolean isHand(){
//        return affectsHand.getValue();
//    }
}
