package com.gmail.inayakitorikhurram.fdmc.mixin.server;

import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldGenerationProgressLogger.class)
public abstract class WorldGenerationProgressLoggerMixin {

    @Mutable
    @Shadow @Final private int totalCount;

    @Shadow public abstract int getProgressPercentage();

    @Shadow private int generatedCount;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/WorldGenerationProgressLogger;totalCount:I", shift = At.Shift.AFTER))
    private void totalCount4(int radius, CallbackInfo ci){
        this.totalCount = (2*radius + 1) * (4 * radius * radius + 4 * radius + 3) / 3;
    }

    @Redirect(method = "setChunkStatus", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    private void removeClamp(Logger logger, String s){
        logger.info(Text.translatable("menu.preparingSpawn", this.getProgressPercentage()).getString() + " = " + this.generatedCount + " / " + this.totalCount);
    }
}
