package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    
    
    //all of these modify the squared chunk distance so random ticks and other stuff actually works
    @ModifyVariable(method = "getSquaredDistance", at = @At("STORE"), ordinal = 0)
    private static double calculateCorrectChunkX(double x3){
        double w4 = Math.floor(0.5 + (x3/FDMCConstants.STEP_DISTANCE) );
        return x3 - w4 * FDMCConstants.STEP_DISTANCE;
    }
    @ModifyVariable(method = "getSquaredDistance", at = @At("STORE"), ordinal = 2)
    private static double calculateCorrectEntityX(double x3){
        double w4 = Math.floor(0.5 + (x3/FDMCConstants.STEP_DISTANCE) );
        return x3 - w4 * FDMCConstants.STEP_DISTANCE;
    }
    @Inject(method = "getSquaredDistance", at = @At("RETURN"), cancellable = true)
    private static void getSquaredDistance4(ChunkPos pos, Entity entity, CallbackInfoReturnable<Double> cir){
        double posW = new ChunkPos4(pos).w;
        double entityW = new Vec4d(entity.getPos()).w;

        double dw = posW - entityW;
        dw = dw * 16; //so it's chunk based

        cir.setReturnValue(cir.getReturnValue() + dw * dw);
    }
}
