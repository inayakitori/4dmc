package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MoveControl.class)
class MoveControlMixin {


    @Shadow
    protected double targetX;
    @Shadow
    @Final
    protected MobEntity entity;
    @Shadow
    protected double speed;
//    protected float sidewaysWMovement;

    // in a tick, if it's a move tick and need to step do that
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getX()D"))
    private double fdmc$stepOnTick(MobEntity entity, Operation<Double> original){
        double entityX3 = original.call(entity);
        int entityW = BlockPos4.of(entity.blockPos).getW4();
        double targetX3 = this.targetX;
        double[] targetXW = FDMCMath.splitX3(targetX3);
        double targetX = targetXW[0];
        int targetW = MathHelper.floor(targetXW[1]);

        double entityDw = (targetW + 0.5d) - Vec4d.of(entity.pos).w;
        int moveDirection = MathHelper.sign(entityDw);
        float movementSpeed = (float) (this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
        ((SidewaysSpeedW)this.entity).setSidewaysSpeedW(moveDirection * movementSpeed);

        int blockDw = targetW - entityW;
        // this makes us move as if we are in the appropriate slice
        return entityX3 + FDMCMath.getOffsetX(blockDw);
    }
}

