package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
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

        Vec4d playerEyePos4 = new Vec4d(playerEyePos);
        Vec4d blockPos4     = new Vec4d(blockPos);

        Vec4d diff = playerEyePos4.subtract(blockPos4);

        double diffSquared = diff.lengthSquared();

        if(diffSquared > 36){
            LOGGER.info("fdmc: block {} ({}) is too far away from {} ({}) at rt({})m", blockPos, blockPos4, playerEyePos, playerEyePos4, diffSquared);
        } else{
            LOGGER.info("fdmc: block {} ({}) is close enough to  {} ({}) at rt({})m", blockPos, blockPos4, playerEyePos, playerEyePos4, diffSquared);
        }

        //TODO math helper
        return diffSquared;
    }
}
