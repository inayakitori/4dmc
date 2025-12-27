package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @WrapMethod(method = "applyElasticity")
    private boolean fdmc$stepOnElasticity(Entity leashHolder, Leashable.LeashData leashData, Operation<Boolean> this$applyElasticity){
        Entity entity$this = (Entity) (Object) this;

        double dw = new Vec4d(leashHolder.pos).subtract(new Vec4d(entity$this.pos)).w;

        int stepDirection = (int) Math.clamp(dw, -1, 1);

        if(stepDirection != 0 && entity$this instanceof CanStep stepping$this) {
            stepping$this.scheduleStep(stepDirection, false);
            return false;
        } else {
            return this$applyElasticity.call(leashHolder, leashData);
        }
    }

    @WrapOperation(method = "getDistanceToCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;distanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    private double fdmc$use4Distance(Vec3d center, Vec3d otherCenter, Operation<Double> this$getDistanceToCenter){

        return new Vec4d(center).distanceTo(new Vec4d(otherCenter), 6.0f);
    }
}
