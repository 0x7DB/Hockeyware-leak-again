package org.hockey.hockeyware.client.features.module.modules.Combat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.util.client.InventoryUtil;

public class AutoTotem extends Module {

    public static AutoTotem INSTANCE;

    public AutoTotem() {
        super("AutoTotem", "Automatically Puts A Totem In Your Offhand", Category.Combat);
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo() {
        return String.valueOf(InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING, true));
    }

    @Override
    public void onUpdate() {
        if (Globals.mc.currentScreen instanceof GuiContainer && !(Globals.mc.currentScreen instanceof GuiInventory))
            return;
        int totemslot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (Globals.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && totemslot != -1) {
            Globals.mc.playerController.windowClick(Globals.mc.player.inventoryContainer.windowId, totemslot, 0, ClickType.PICKUP, Globals.mc.player);
            Globals.mc.playerController.windowClick(Globals.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, Globals.mc.player);
            Globals.mc.playerController.windowClick(Globals.mc.player.inventoryContainer.windowId, totemslot, 0, ClickType.PICKUP, Globals.mc.player);
            Globals.mc.playerController.updateController();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            Globals.mc.player.connection.sendPacket(new CPacketEntityAction(Globals.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }
}
