package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LookControl.class)
public class LookControlMixin {
    @ModifyVariable(method = "getTargetPitch", at = @At(value = "STORE",ordinal = 0))
    private double fdmc$modifyPitch(double value){
        return FDMCMath.splitX3(value)[0];
    }


    @ModifyVariable(method = "getTargetYaw", at = @At(value = "STORE",ordinal = 0))
    private double fdmc$modifyYaw(double value){
        return FDMCMath.splitX3(value)[0];
    }

}
