package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {
    @ModifyVariable(method = "shootAt", at = @At(value = "STORE", ordinal = 0))
    private double fdmc$modifyTargetX(double value){
        return FDMCMath.splitX3(value)[0];
    }
}
