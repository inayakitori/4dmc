package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.getTargetX;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {
        "net.minecraft.entity.ai.control.MoveControl",
        "net.minecraft.entity.ai.control.FlightMoveControl",
        "net.minecraft.entity.ai.control.AquaticMoveControl"
})
abstract class MoveControlMixins implements getTargetX {

    // in a tick, if it's a move tick and need to step do that
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getX()D"))
    private double fdmc$stepOnTick(MobEntity entity, Operation<Double> original){
        return MixinUtil.getOffsetEntityXAndStepIfNecessary(entity, original.call(entity), targetX());
    }
}

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$PhantomMoveControl")
abstract class PhantomEntity$PhantomMoveControlMixin implements getTargetX {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PhantomEntity;getX()D"))
    private double fdmc$stepOnTick(PhantomEntity entity, Operation<Double> original){
        return MixinUtil.getOffsetEntityXAndStepIfNecessary(entity, original.call(entity), entity.targetPosition.x);
    }
}

@Mixin(MoveControl.class)
abstract class MoveControlMixin implements getTargetX{
    @Shadow
    protected double targetX;

    @Override
    public double targetX() {
        return this.targetX;
    }
}