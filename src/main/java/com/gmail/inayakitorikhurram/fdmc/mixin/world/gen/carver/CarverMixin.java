package com.gmail.inayakitorikhurram.fdmc.mixin.world.gen.carver;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Carver.class)
public class CarverMixin {

    @ModifyVariable(method = "canCarveBranch", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private static double modifiedBranchPositionX(double x3){
        return 0;
    }
    @Redirect(method = "canCarveBranch", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkPos;getCenterX()I"))
    private static int modifiedBranchPositionPos(ChunkPos instance){
        double x3 = instance.getCenterX();
        double[] xw = FDMCMath.splitX3(x3);
        return (int) xw[0];
    }

}
