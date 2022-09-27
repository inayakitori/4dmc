package com.gmail.inayakitorikhurram.fdmc.mixin;


import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {


    @Final
    @Shadow
    public static int START_TICKET_CHUNK_RADIUS = 5;
    @Final
    @Shadow
    private static int START_TICKET_CHUNKS = 243;

    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 11))
    private int injectedRangeWhenPreparing(int value) {
        return 5;
    }

    /**
    @ModifyConstant(method = "loadWorld", constant = @Constant(intValue = 11))
    private int injectedRangeWhenLoading(int value) {
        return 5;
    }

    @ModifyConstant(method = "setupSpawn", constant = @Constant(intValue = 11))
    private static int injectedRangeWhenSpawning(int value) {
        return 5;
    }
**/
    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 441))
    private int injectedChunkCount(int value) {
        return 243;
    }
}
