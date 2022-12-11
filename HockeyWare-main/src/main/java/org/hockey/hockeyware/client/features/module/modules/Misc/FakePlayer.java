package org.hockey.hockeyware.client.features.module.modules.Misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.entity.EntityWorldEvent;
import org.hockey.hockeyware.client.events.network.DisconnectEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

import java.util.concurrent.ThreadLocalRandom;

public class FakePlayer extends Module {
    public static FakePlayer INSTANCE;

    public FakePlayer() {
        super("FakePlayer", "Spawns In A Fake Player", Category.Misc);
        INSTANCE = this;
        setExempt(true);
    }

    public static Setting<Boolean> inventory = new Setting<>("Inventory", true);

    public static Setting<Boolean> health = new Setting<>("Health", true);

    private int id = -1;

    @Override
    public void onEnable() {
        super.onEnable();

        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getGameProfile().getId(), "FakePlayer"));

        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYaw;

        if (inventory.getValue()) {
            fakePlayer.inventory.copyInventory(mc.player.inventory);
            fakePlayer.inventoryContainer = mc.player.inventoryContainer;
        }

        if (health.getValue()) {
            fakePlayer.setHealth(mc.player.getHealth());
            fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());
        }

        fakePlayer.setSneaking(mc.player.isSneaking());
        fakePlayer.setPrimaryHand(mc.player.getPrimaryHand());

        id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        mc.world.addEntityToWorld(id, fakePlayer);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.world.removeEntityFromWorld(id);
        id = -1;
    }

    @SubscribeEvent
    public void onEntityRemove(EntityWorldEvent.EntityRemoveEvent event) {
        if (event.getEntity().equals(mc.player)) {

            mc.world.removeEntityFromWorld(id);
            id = -1;

            toggle(true);
        }
    }

    @SubscribeEvent
    public void onDisconnect(DisconnectEvent event) {

        toggle(true);
    }
}