package com.gmail.inayakitorikhurram.fdmc.mixin.client;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.client.ScreenshotManager;
import com.squareup.gifencoder.FloydSteinbergDitherer;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint.placeW;
import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.FDMC_TEMP_FOLDER;
import static net.minecraft.client.util.ScreenshotRecorder.SCREENSHOTS_DIRECTORY;

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
