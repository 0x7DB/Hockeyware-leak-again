package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Combat.AutoCrystal;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Replenish extends Module {
    public static Replenish INSTANCE;

    public Replenish() {
        super("Replenish", "Allows You To Automatically Replinish Your Hotbar", Category.Player);
        INSTANCE = this;
    }

    public static Setting<Double> percent = new Setting<>("Percent", 1.0, 1.0, 99.0);

    public static Setting<Double> delay = new Setting<>("Delay", 100.0, 100.0, 1000.0);

    public static Setting<Boolean> wait = new Setting<>("Wait", true);

    private final Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();

    private final Timer timer = new Timer();

    private int refillSlot = -1;

    @Override
    public void onDisable() {
        super.onDisable();

        // reset values
        hotbar.clear();
        refillSlot = -1;
    }

    @Override
    public void onUpdate() {

        if (refillSlot == -1) {

            for (int i = 0; i < 9; ++i) {

                ItemStack stack = mc.player.inventory.getStackInSlot(i);

                if (hotbar.getOrDefault(i, null) == null) {

                    if (stack.getItem().equals(Items.AIR)) {
                        continue;
                    }

                    hotbar.put(i, stack);
                    continue;
                }

                double percentage = ((double) stack.getCount() / (double) stack.getMaxStackSize()) * 100.0;

                if (percentage <= percent.getValue()) {

                    if (stack.getItem().equals(Items.END_CRYSTAL) && wait.getValue() && AutoCrystal.INSTANCE.isOn()) {
                        continue;
                    }

                    if (!timer.passedTime(delay.getValue().longValue(), Timer.Format.MILLISECONDS)) {

                        refillSlot = i;
                    }

                    else {

                        fillStack(i, stack);

                        timer.resetTime();
                    }

                    break;
                }
            }
        }

        else {
            if (timer.passedTime(delay.getValue().longValue(), Timer.Format.MILLISECONDS)) {

                fillStack(refillSlot, hotbar.get(refillSlot));

                timer.resetTime();
                refillSlot = -1;
            }
        }
    }

    private void fillStack(int slot, ItemStack stack) {

        if (slot != -1 && stack != null) {
            int replenishSlot = -1;

            for (int i = 9; i < 36; ++i) {
                ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

                if (!itemStack.isEmpty()) {

                    if (!stack.getDisplayName().equals(itemStack.getDisplayName())) {
                        continue;
                    }

                    if (stack.getItem() instanceof ItemBlock) {
                        if (!(itemStack.getItem() instanceof ItemBlock)) {
                            continue;
                        }

                        ItemBlock hotbarBlock = (ItemBlock) stack.getItem();
                        ItemBlock inventoryBlock = (ItemBlock) itemStack.getItem();

                        if (!hotbarBlock.getBlock().equals(inventoryBlock.getBlock())) {
                            continue;
                        }
                    }

                    else {
                        if (!stack.getItem().equals(itemStack.getItem())) {
                            continue;
                        }
                    }

                    replenishSlot = i;
                }
            }

            if (replenishSlot != -1) {

                int total = stack.getCount() + mc.player.inventory.getStackInSlot(replenishSlot).getCount();

                mc.playerController.windowClick(0, replenishSlot, 0, ClickType.PICKUP, mc.player);

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);

                if (total >= stack.getMaxStackSize()) {
                    mc.playerController.windowClick(0, replenishSlot, 0, ClickType.PICKUP, mc.player);
                }

                refillSlot = -1;
            }
        }
    }
}