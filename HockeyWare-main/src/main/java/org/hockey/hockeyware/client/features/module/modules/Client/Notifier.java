package org.hockey.hockeyware.client.features.module.modules.Client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.events.client.ModuleToggleEvent;
import org.hockey.hockeyware.client.events.player.EntitySpawnEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Combat.AutoTotem;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.Timer;
import org.hockey.hockeyware.client.util.client.ClientMessage;
import org.hockey.hockeyware.client.util.client.InventoryUtil;

public class Notifier extends Module {

    public static Notifier INSTANCE;
    public static final Setting<Boolean> modules = new Setting<>("Modules", true);
    //public final Setting<Boolean> totemPops = new Setting<>("TotemPops", false);
    public final Setting<Boolean> visualrange = new Setting<>("VisualRange", true);
    public final Setting<Boolean> totemwarning = new Setting<>("NoTotemWarning", false);

    private final Timer timer = new Timer();

    public Notifier() {
        super("Notifier", "Enables Chat Notifications For Certain Modules/Events", Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING, true) != 0 && totemwarning.getValue()) {
            if (timer.hasReached(250)) {
                if (AutoTotem.INSTANCE.isOn()) {
                    ClientMessage.sendOverwriteClientMessage("There Is No Totem In Your Offhand");
                    timer.reset();
                }
            }
        }
    }

    @SubscribeEvent
    public void onModuleToggled(ModuleToggleEvent event) {
        if (this.modules.getValue()) {
            Module module = event.getModule();
            if (!("ClickGUI".equals(module.getName()))) {
                if (!("Burrow".equals(module.getName()))) {
                    if (!("ChestSwap".equals(module.getName()))) {
                        if (!("IRC".equals(module.getName())))
                            ClientMessage.sendMessage(module.getName() + ChatFormatting.BOLD + (module.isToggled(true) ? ChatFormatting.GREEN + " Enabled" : ChatFormatting.RED + " Disabled"));
                        else
                            ClientMessage.sendMessage(module.getName() + ChatFormatting.BOLD + (module.isToggled(true) ?
                                    ChatFormatting.GREEN + " Enabled " + ChatFormatting.RESET + "With The Prefix " + ChatFormatting.BOLD + ((IRC) module).getPrefix() : ChatFormatting.RED + " Disabled"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!fullNullCheck()) return;
        if (visualrange.getValue() && event.getEntity() instanceof EntityPlayer && event.getEntity() != mc.player && event.getEntity() != null) {
            if (event.getType().equals(EntitySpawnEvent.Type.Spawn)) {
                if (event.getEntity().equals(getHockey().friendManager.getPlayer())) {
                    ClientMessage.sendOverwriteClientMessage(ChatFormatting.AQUA + event.getEntity().getName() + ChatFormatting.RESET + " Has Entered Visual Range");
                } else {
                    ClientMessage.sendOverwriteClientMessage(event.getEntity().getName() + " Has Entered Visual Range");
                }
            }
            if (event.getType().equals(EntitySpawnEvent.Type.Despawn)) {
                if (event.getEntity().equals(getHockey().friendManager.getPlayer())) {
                    ClientMessage.sendOverwriteClientMessage(ChatFormatting.AQUA + event.getEntity().getName() + ChatFormatting.RESET + " Has Left Visual Range");
                } else if (!mc.getSession().getUsername().equals(event.getEntity().getName())) {
                    ClientMessage.sendOverwriteClientMessage(event.getEntity().getName() + " Has Left Visual Range");
                }
            }
        }
    }
}
