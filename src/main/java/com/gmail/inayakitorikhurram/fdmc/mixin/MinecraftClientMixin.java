package com.gmail.inayakitorikhurram.fdmc.mixin;

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

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Final private ResourcePackManager resourcePackManager;

    @Shadow @Final @Mutable
    private ReloadableResourceManagerImpl resourceManager;

    @Shadow @Final private static CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getBackendDescription()Ljava/lang/String;", shift = At.Shift.BEFORE))
    private void main(RunArgs args, CallbackInfo ci) {
        if (FabricDataGenHelper.ENABLED && this.resourceManager == null) {
            this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
            resourcePackManager.scanPacks();

            List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
            this.resourceManager.reload(Util.getMainWorkerExecutor(), MinecraftClient.getInstance(), COMPLETED_UNIT_FUTURE, list);
        }
    }
}
