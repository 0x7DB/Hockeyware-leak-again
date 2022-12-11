package org.hockey.hockeyware.client.features.module.modules.Movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.events.client.KeyDownEvent;
import org.hockey.hockeyware.client.events.network.UpdateTimerEvent;
import org.hockey.hockeyware.client.events.player.PlayerUpdateMoveEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.world.BlockUtil;
import org.lwjgl.input.Keyboard;

import static org.hockey.hockeyware.client.features.module.modules.Movement.NoSlow.ItemMode.NCPStrict;
import static org.hockey.hockeyware.client.manager.EventManager.KEYS;

public class NoSlow extends Module {
    public static NoSlow INSTANCE;

    public NoSlow() {
        super("NoSlow", "Allows You To Go Faster",  Category.Movement);
        INSTANCE = this;
    }

    boolean sneakingFlag;

    public static Setting<NoSlow.ItemMode> itemMode = new Setting<>("ItemMode", NCPStrict);
    public static Setting<Boolean> items = new Setting<>("Items", true);
    public static Setting<Boolean> guiMove = new Setting<>("GuiMove", true);
    public static Setting<Float> arrowLook = new Setting<>("Arrow", 0.0F, 5.0F, 10.0F);
    public static Setting<NoSlow.CobWebMode> cobWebMode = new Setting<>("CobWebMode", CobWebMode.Motion);
    public static Setting<Float> webHorizontalFactor = new Setting<>("WebHSpeed", 2.0f, 0.0f, 100.0f);
    public static Setting<Float> webVerticalFactor = new Setting<>("WebVSpeed", 2.0f, 0.0f, 100.0f);
    public static Setting<Float> cobwebTimerSpeed = new Setting<>("CobwebTimerSpeed", 10.0f, 1.0f, 15.0f);
    public static Setting<Boolean> sneak = new Setting<>("Sneak", true);


    @Override
    public void onUpdate(){

        if (guiMove.getValue() && isInScreen()) {
            
            for (KeyBinding binding : KEYS) {
                KeyBinding.setKeyBindState(binding.getKeyCode(), Keyboard.isKeyDown(binding.getKeyCode()));
                binding.setKeyConflictContext(ConflictContext.FAKE_CONTEXT);
            }
            
            if (arrowLook.getValue() != 0) {
                if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    mc.player.rotationPitch -= arrowLook.getValue();
                }

                else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    mc.player.rotationPitch += arrowLook.getValue();
                }

                else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    mc.player.rotationYaw += arrowLook.getValue();
                }

                else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    mc.player.rotationYaw -= arrowLook.getValue();
                }
                
                mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch, -90, 90);
            }
        }

        else {
            // reset key conflict
            for (KeyBinding binding : KEYS) {
                binding.setKeyConflictContext(KeyConflictContext.IN_GAME);
            }
        }
    }

    @SubscribeEvent
    public void onKeyDown(KeyDownEvent event) {
        
        if (isInScreen()) {
            
            if (guiMove.getValue()) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public void onUpdateMove(PlayerUpdateMoveEvent event) {
        if (itemMode.getValue() == ItemMode._2B2TSneak && !mc.player.isSneaking() && !mc.player.isRiding()) {
            if (mc.player.isHandActive()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneakingFlag = true;
            }
            else if (sneakingFlag) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneakingFlag = false;
            }
        }

        if (cobWebMode.getValue() == CobWebMode.Motion && mc.player.isInWeb) {
            mc.player.motionX *= webHorizontalFactor.getValue();
            mc.player.motionZ *= webHorizontalFactor.getValue();
            mc.player.motionY *= webVerticalFactor.getValue();
        }
    }

    @SubscribeEvent
    public void onUpdateTimer(UpdateTimerEvent event) {
        if (mc.world == null || mc.player == null) return;

        BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
        if (cobWebMode.getValue() == CobWebMode.Timer && (mc.world.getBlockState(BlockUtil.extrudeBlock(playerPos, EnumFacing.UP)).getBlock() == Blocks.WEB || mc.world.getBlockState(playerPos).getBlock() == Blocks.WEB || mc.world.getBlockState(BlockUtil.extrudeBlock(playerPos, EnumFacing.DOWN)).getBlock() == Blocks.WEB)) {
            event.timerSpeed = cobwebTimerSpeed.getValue();
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if ((items.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) || (sneak.getValue() && mc.player.isSneaking())) {
            event.getMovementInput().moveForward /= 0.2f;
            event.getMovementInput().moveStrafe /= 0.2f;
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && items.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            switch (itemMode.getValue()) {
                case NCPStrict: {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
                    break;
                }

                case _2B2TBypass: {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    break;
                }
            }
        }
    }

    public boolean isInScreen() {
        return mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiRepair);
    }

    enum ItemMode {
        Normal,
        NCPStrict,
        _2B2TSneak,
        _2B2TBypass
    }

    public enum CobWebMode {
        None,
        Cancel,
        Motion,
        Timer
    }


    public enum ConflictContext implements IKeyConflictContext {
        
        FAKE_CONTEXT {
            @Override
            public boolean isActive() {
                return false;
            }
            @Override
            public boolean conflicts(IKeyConflictContext other) {
                return false;
            }
        }
    }
    }