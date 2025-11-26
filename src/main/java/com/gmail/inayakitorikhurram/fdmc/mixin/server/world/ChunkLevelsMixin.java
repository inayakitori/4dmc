package com.gmail.inayakitorikhurram.fdmc.mixin.server.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.world.ChunkLevels;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkLevels.class)
public class ChunkLevelsMixin {
    @Mutable
    @Shadow
    @Final
    public static int FULL_GENERATION_REQUIRED_LEVEL;

    @Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ChunkLevels;FULL_GENERATION_REQUIRED_LEVEL:I", opcode = Opcodes.PUTSTATIC))
    private static void fdmc$modifyStartRegion(int value){
        FULL_GENERATION_REQUIRED_LEVEL = value;
    }
}
