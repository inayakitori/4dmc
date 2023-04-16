package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkNoiseSampler.class)
public abstract class ChunkNoiseSamplerMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static int modifyStartX(int x){
        int w4 = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        return x - w4 * FDMCConstants.STEP_DISTANCE;
    }

    @Inject(method = "blockX", at = @At("RETURN"), cancellable = true)
    private void modifiedBlockX(CallbackInfoReturnable<Integer> cir){
        int x = cir.getReturnValue();
        int w4 = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        int x4 = x - w4 * FDMCConstants.STEP_DISTANCE;
        cir.setReturnValue(x4);
    }

}
