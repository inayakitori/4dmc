package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Client4Access;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.FDMC_TEMP_FOLDER;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements Client4Access {

    private boolean screenshotState;

    @Shadow @Final private ResourcePackManager resourcePackManager;

    @Shadow @Final @Mutable
    private ReloadableResourceManagerImpl resourceManager;

    @Shadow @Final private static CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;

    @Shadow @Final public File runDirectory;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getBackendDescription()Ljava/lang/String;", shift = At.Shift.BEFORE))
    private void main(RunArgs args, CallbackInfo ci) {
        if (this.resourceManager == null) {
            this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
            resourcePackManager.scanPacks();

            List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
            this.resourceManager.reload(Util.getMainWorkerExecutor(), MinecraftClient.getInstance(), COMPLETED_UNIT_FUTURE, list);
        }
        File temp_directory = new File(this.runDirectory, FDMC_TEMP_FOLDER);
        temp_directory.mkdir();
    }

    @Override
    public boolean getScreenshotState() {
        return this.screenshotState;
    }

    @Override
    public void setScreenshotState(boolean screenshotState) {
        this.screenshotState = screenshotState;
    }
}
