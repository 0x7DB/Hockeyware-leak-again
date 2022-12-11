package org.hockey.hockeyware.client.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.MainMenu;
import org.hockey.hockeyware.client.gui.mainMenu.customMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ Minecraft.class })
public abstract class MixinMinecraft {
    @Shadow
    public abstract void displayGuiScreen(@Nullable final GuiScreen p0);

    @Shadow
    public static Minecraft instance;

    @Inject(method = "init", at = @At("TAIL"))
    private void initHook(CallbackInfo ci) {
        HockeyWare.INSTANCE.init();
    }

    @Inject(method = {"runTick()V"}, at = {@At(value = "RETURN")})
    private void runTick(CallbackInfo callbackInfo) {
        if (HockeyWare.INSTANCE.moduleManager.getModuleByClass(MainMenu.class).isToggled(true)) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen) new customMainMenu());
            }
        }
    }
//
//    @Inject(method = {"displayGuiScreen"}, at = {@At("HEAD")})
//    private void displayGuiScreen(final GuiScreen screen, final CallbackInfo ci) {
//        if (screen instanceof GuiMainMenu) {
//            this.displayGuiScreen(new GuiCustomMainScreen());
//        }
//    }
}

