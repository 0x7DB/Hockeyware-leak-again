package org.hockey.hockeyware.client.features.module.modules.Combat;

import net.minecraft.block.*;
import net.minecraft.block.BlockSkull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Client.Notifier;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.BurrowUtil;
import org.hockey.hockeyware.client.util.client.ClientMessage;

public class Burrow
        extends Module {

    public static Burrow INSTANCE;
    private final Setting<Integer> offset = new Setting<>("Offset", 3, -5, 5);
    private final Setting<Boolean> ground = new Setting<>("GroundCheck", true);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", false);
    private final Setting<Boolean> center = new Setting<>("Center", true);
    private final Setting<Boolean> echest = new Setting<>("UseEchest", false);
    private final Setting<Boolean> anvil = new Setting<>("UseAnvil", false);
    private final Setting<Boolean> web = new Setting<>("UseWeb", false);
    private final Setting<Boolean> skull = new Setting<>("UseSkull", false);

    private BlockPos originalPos;
    private int oldSlot = -1;

    public Burrow() {
        super("Burrow", "Allows You To Glitch Inside Certain Blocks", Category.Combat);
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        super.onEnable();
        this.originalPos = new BlockPos(Burrow.mc.player.posX, Burrow.mc.player.posY, Burrow.mc.player.posZ);
        if (Burrow.mc.world.getBlockState(new BlockPos(Burrow.mc.player.posX, Burrow.mc.player.posY, Burrow.mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) || this.intersectsWithEntity(this.originalPos)) {
            this.toggle(true);
            return;
        }
        if (this.center.getValue()) {
            double x = Burrow.mc.player.posX - Math.floor(Burrow.mc.player.posX);
            double z = Burrow.mc.player.posZ - Math.floor(Burrow.mc.player.posZ);
            if (x <= 0.3 || x >= 0.7) {
                double d = x = x > 0.5 ? 0.69 : 0.31;
            }
            if (z < 0.3 || z > 0.7) {
                z = z > 0.5 ? 0.69 : 0.31;
            }
            Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Math.floor(Burrow.mc.player.posX) + x, Burrow.mc.player.posY, Math.floor(Burrow.mc.player.posZ) + z, Burrow.mc.player.onGround));
            Burrow.mc.player.setPosition(Math.floor(Burrow.mc.player.posX) + x, Burrow.mc.player.posY, Math.floor(Burrow.mc.player.posZ) + z);
        }
        this.oldSlot = Burrow.mc.player.inventory.currentItem;
    }

    @Override
    public void onUpdate() {
        if (this.ground.getValue() && !Burrow.mc.player.onGround) {
            this.toggle(true);
            return;
        }

        if (this.anvil.getValue() && BurrowUtil.findHotbarBlock(BlockAnvil.class) != -1) {
            BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockAnvil.class));
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendMessage("Burrowed Into Block");
            }}
        } else if (this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) != -1 : BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1) {
            BurrowUtil.switchToSlot(this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) : BurrowUtil.findHotbarBlock(BlockObsidian.class));
            if (this.web.getValue() && BurrowUtil.findHotbarBlock(BlockWeb.class) != -1);
            BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockWeb.class));
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendMessage("Burrowed Into Block");
            }}
        } else if (this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) != -1 : BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1) {
            BurrowUtil.switchToSlot(this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) : BurrowUtil.findHotbarBlock(BlockObsidian.class));
            if (this.skull.getValue() && BurrowUtil.findHotbarBlock(BlockSkull.class) != -1)
                BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockSkull.class));
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendMessage("Burrowed Into Block");
            }}
        } else if (this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) != -1 : BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1) {
            BurrowUtil.switchToSlot(this.echest.getValue() ? BurrowUtil.findHotbarBlock(BlockEnderChest.class) : BurrowUtil.findHotbarBlock(BlockObsidian.class));
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendMessage("Burrowed Into Block");
            }}
        } else {
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendMessage("No Selected Burrow Blocks In Hotbar");
                this.toggle(true);
                return;
            }}
        }
        Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.41999998688698, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.7531999805211997, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.00133597911214, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.16610926093821, Burrow.mc.player.posZ, true));
        BurrowUtil.placeBlock(this.originalPos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
        Burrow.mc.player.connection.sendPacket(new CPacketPlayer.Position(Burrow.mc.player.posX, Burrow.mc.player.posY + (double) this.offset.getValue(), Burrow.mc.player.posZ, false));
        Burrow.mc.player.connection.sendPacket(new CPacketEntityAction((Entity) Burrow.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        Burrow.mc.player.setSneaking(false);
        BurrowUtil.switchToSlot(this.oldSlot);
        this.toggle(true);
    }

    private boolean intersectsWithEntity(BlockPos pos) {
        for (Entity entity : Burrow.mc.world.loadedEntityList) {
            if (entity.equals((Object) Burrow.mc.player) || entity instanceof EntityItem || !new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox()))
                continue;
            return true;
        }
        return false;
    }
}