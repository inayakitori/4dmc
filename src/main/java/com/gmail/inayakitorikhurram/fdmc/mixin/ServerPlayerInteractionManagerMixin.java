package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow @Final private static Logger LOGGER;

    @Redirect(method = "processBlockBreakingAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"
    ))
    private double squaredDistanceInjection(Vec3d playerEyePos, Vec3d blockPos){

        double[] playerEyePos4 = FDMCMath.toPos4(playerEyePos);
        double[] blockPos4 = FDMCMath.toPos4(blockPos);

        double[] diff = new double[]{
                playerEyePos4[0] - blockPos4[0],
                playerEyePos4[1] - blockPos4[1],
                playerEyePos4[2] - blockPos4[2],
                playerEyePos4[3] - blockPos4[3]
        };

        double diffSquared =
                diff[0] * diff[0] +
                diff[1] * diff[1] +
                diff[2] * diff[2] +
                diff[3] * diff[3] ;

        if(diffSquared > 36){
            LOGGER.info("fdmc: block {} ({}) is too far away from {} ({}) at rt({})m", blockPos, blockPos4, playerEyePos, playerEyePos4, diffSquared);
        } else{
            LOGGER.info("fdmc: block {} ({}) is close enough to  {} ({}) at rt({})m", blockPos, blockPos4, playerEyePos, playerEyePos4, diffSquared);
        }

        //TODO math helper
        return diffSquared;
    }
}
