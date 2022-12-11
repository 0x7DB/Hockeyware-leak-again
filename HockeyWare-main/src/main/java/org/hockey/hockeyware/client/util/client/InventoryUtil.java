package org.hockey.hockeyware.client.util.client;

import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import org.hockey.hockeyware.client.features.Globals;

public class InventoryUtil implements Globals {

    public static int itemCount;

    public static int getItemSlot(Item items) {
        for (int i = 0; i < 36; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == items) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }
        return -1;
    }

    public static ItemStack getItemStack(int id) {
        try {
            return mc.player.inventory.getStackInSlot(id);
        } catch (NullPointerException e) {
            return null;
        }
    }


    public static void clickSlot(int id) {
        if (id != -1) {
            try {
                mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id), 0, ClickType.PICKUP, mc.player);
            } catch (Exception ignored) {

            }
        }
    }


    public static int getHotbarSlot(Item items) {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == items) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int getItemCount(Item item, boolean includeOffhand) {
        if (!includeOffhand) {
            itemCount = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum();
        } else if (includeOffhand) {
            itemCount = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum() + mc.player.inventory.offHandInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum();
        }
        return itemCount;
    }


    public static int getClickSlot(int id) {
        if (id == -1) {
            return id;
        }

        if (id < 9) {
            id += 36;
            return id;
        }

        if (id == 39) {
            id = 5;
        } else if (id == 38) {
            id = 6;
        } else if (id == 37) {
            id = 7;
        } else if (id == 36) {
            id = 8;
        } else if (id == 40) {
            id = 45;
        }

        return id;
    }


    public static void switchToSlot(final int slot, final boolean silent) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        if (!silent) mc.player.inventory.currentItem = slot;
    }

    public static boolean isInHotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            mc.player.inventory.getStackInSlot(i);
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

}
