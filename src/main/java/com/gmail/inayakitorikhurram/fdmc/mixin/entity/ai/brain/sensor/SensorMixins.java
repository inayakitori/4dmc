package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.brain.sensor;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NearestLivingEntitiesSensor.class)
class NearestLivingEntitiesSensorMixin{
    @WrapOperation(method = "sense", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box box, double x, double y, double z, Operation<Box> original){
        return Box4.converted(original.call(box, x, y, z)).expand(0, x / FDMCConstants.FOLLOW_RANGE_W_SCALE);
    }
}
