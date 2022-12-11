package org.hockey.hockeyware.client.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.client.KeyDownEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow
    private boolean pressed;

    @Shadow
    private int keyCode;

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    public void onIsKeyDown(CallbackInfoReturnable<Boolean> info) {
        KeyDownEvent keyDownEvent = new KeyDownEvent(keyCode, pressed);
        HockeyWare.EVENT_BUS.post(keyDownEvent);

        if (keyDownEvent.isCanceled()) {
            info.setReturnValue(keyDownEvent.isPressed());
        }
    }
}