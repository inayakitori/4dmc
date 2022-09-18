package com.gmail.inayakitorikhurram.fdmc.mixin;


import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 11))
    private int injectedRange(int value) {
        return 5;
    }

    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 441))
    private int injectedChunkCount(int value) {
        return 119;
    }

}
