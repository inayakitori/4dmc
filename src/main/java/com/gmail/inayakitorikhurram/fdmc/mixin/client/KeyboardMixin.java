package com.gmail.inayakitorikhurram.fdmc.mixin.client;

import com.gmail.inayakitorikhurram.fdmc.client.ScreenshotManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint.placeW;

//look this is so messy. It needs refactoring, but I honestly just want to
// get this working right now. technical debt go brrr
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;
    private ScreenshotManager screenshotManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(MinecraftClient client, CallbackInfo ci){
        this.screenshotManager = new ScreenshotManager(client);
    }

    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z", ordinal = 1))
    private boolean initiate4Screenshot(KeyBinding screenshotKey, int keyCode, int scanCode){
        if (!screenshotKey.matchesKey(keyCode, scanCode)) return false;
        boolean shouldToggleScreenshotState = placeW.isPressed();
        return screenshotManager.try4Screenshot(shouldToggleScreenshotState);
    }

}
