package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkNoiseSampler.class)
public abstract class ChunkNoiseSamplerMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static int modifyStartX(int x){
        return FDMCMath.getX4(x);
    }

    @Inject(method = "blockX", at = @At("RETURN"), cancellable = true)
    private void modifiedBlockX(CallbackInfoReturnable<Integer> cir){
        int x3 = cir.getReturnValue();
        int x4 = FDMCMath.getX4(x3);
        cir.setReturnValue(x4);
    }

}
