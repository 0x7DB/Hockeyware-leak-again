package org.hockey.hockeyware.client.manager;

import com.google.common.base.Strings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.events.player.*;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.util.Timer;

import java.util.Objects;
import java.util.UUID;

public class EventManager implements Globals {
    public static final KeyBinding[] KEYS;
    private static EventManager INSTANCE;

    static {
        KEYS = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
    }

    private final Timer logoutTimer = new Timer();
    public boolean hasRan;

    public EventManager() {
    }

    public static EventManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventManager();
        }

        return INSTANCE;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (fullNullCheck() && event.getEntity() == mc.player) {
            getHockey().popManager.onUpdate();
            for (Module module : getHockey().moduleManager.getModules()) {
                if (module.isToggled(true)) {
                    module.onUpdate();
                }
            }
        }
    }
    @SubscribeEvent
    public void onKnockback(LivingKnockBackEvent event) {
        KnockBackEvent knockBackEvent = new KnockBackEvent();
        HockeyWare.EVENT_BUS.post(knockBackEvent);

        if (knockBackEvent.isCanceled()) {
            event.setCanceled(true);
        }
    }

//    @SubscribeEvent
//    public void onTick(TickEvent.ClientTickEvent event) {
//        if (HUD.copyCoords.getValue() && fullNullCheck()) {
//            String coordinates = (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ;
//            StringSelection stringSelection = new StringSelection(coordinates);
//            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//            clipboard.setContents(stringSelection, null);
//            ClientMessage.sendMessage("Copied Coordinates To Clipboard");
//            HUD.copyCoords.setValue(false);
//        }
//        if (SelfWeb.enableInHole.getValue() && SelfWeb.isInHole(mc.player) && !SelfWeb.isInBurrow(mc.player) && fullNullCheck()) {
//            SelfWeb.INSTANCE.setToggled(true);
//        }
//    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (fullNullCheck()) {
            for (Module module : getHockey().moduleManager.getModules()) {
                if (module.isOn()) {
                    module.onRender3D();
                }
            }
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        ItemInputUpdateEvent itemInputUpdateEvent = new ItemInputUpdateEvent(event.getMovementInput());
        HockeyWare.EVENT_BUS.post(itemInputUpdateEvent);
    }

    @SubscribeEvent
    public void onRenderHudText(RenderGameOverlayEvent.Text event) {
        if (fullNullCheck()) {
            for (Module module : getHockey().moduleManager.getModules()) {
                if (module.isToggled(true)) {
                    module.onRender2D();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                HockeyWare.EVENT_BUS.post(new TotemPopEvent((EntityPlayer) packet.getEntity(mc.world)));
            }
        } else if (event.getPacket() instanceof SPacketPlayerListItem && fullNullCheck() && logoutTimer.hasReached(1L)) {
            SPacketPlayerListItem packet = event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction())) {
                return;
            }
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null).forEach(data -> {
                UUID id = data.getProfile().getId();
                switch (packet.getAction()) {
                    case ADD_PLAYER: {
                        String name = data.getProfile().getName();
                        HockeyWare.EVENT_BUS.post(new ConnectionEvent(id, name, ConnectionEvent.Type.Join));
                        break;
                    }
                    case REMOVE_PLAYER: {
                        EntityPlayer entity = mc.world.getPlayerEntityByUUID(id);
                        if (entity != null) {
                            String logoutName = entity.getName();
                            HockeyWare.EVENT_BUS.post(new ConnectionEvent(entity, id, logoutName, ConnectionEvent.Type.Leave));
                            break;
                        }
                        HockeyWare.EVENT_BUS.post(new ConnectionEvent(id, null, ConnectionEvent.Type.Other));
                    }
                    default:
                        break;
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerJoinedServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        HockeyWare.LOGGER.info("Joined");
    }


}