package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Client.Notifier;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.ClientMessage;
import org.hockey.hockeyware.client.util.client.InventoryUtil;

public class ChestSwap extends Module {

    public ChestSwap() {
        super("ChestSwap", "Allows You To Switch Between Elytra And Chestplate With A Keybind", Category.Player);
    }

    @Override
    public void onEnable() {
        ItemStack itemStack = InventoryUtil.getItemStack(38);
        assert itemStack != null;
        if (itemStack.getItem() == Items.ELYTRA) {
            int slot = getChestPlateSlot();
            if (slot != -1) {
                InventoryUtil.clickSlot(slot);
                InventoryUtil.clickSlot(38);
                InventoryUtil.clickSlot(slot);
                if (Notifier.INSTANCE.isOn()){
                    if (Notifier.modules.getValue()){
                    ClientMessage.sendOverwriteClientMessage("Switched To Chestplate");
                }}
            } else {
                ClientMessage.sendMessage("No Chestplate Was Found In Inventory", true);
            }
        } else if (InventoryUtil.getItemCount(Items.ELYTRA, true) != 0) {
            int slot = InventoryUtil.getItemSlot(Items.ELYTRA);
            InventoryUtil.clickSlot(slot);
            InventoryUtil.clickSlot(38);
            InventoryUtil.clickSlot(slot);
            if (Notifier.INSTANCE.isOn()){
                if (Notifier.modules.getValue()){
                ClientMessage.sendOverwriteClientMessage("Switched to Elytra");
            }}
        } else {
            ClientMessage.sendMessage("No Elytra Was Found In Inventory", true);
        }
        this.toggle(true);
    }

    public int getChestPlateSlot() {
        Item[] items = {Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE};

        for (Item item : items) {
            if (InventoryUtil.getItemCount(item, true) != 0) {
                return InventoryUtil.getItemSlot(item);
            }
        }
        return -1;
    }
}

