package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PositionInterpolator.class)
public class PositionInterpolatorMixin {
    @Shadow
    @Final
    private Entity entity;
    @Shadow
    @Final
    private PositionInterpolator.Data data;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 0))
    private double fdmc$lerpXCorrectly(double delta, double start, double end, Operation<Double> original, @Share("w")LocalDoubleRef w){
        w.set(original.call(delta, Vec4d.of(entity.pos).w, Vec4d.of(data.pos).w));
        return original.call(delta, Vec4d.of(entity.pos).x4, Vec4d.of(data.pos).x4);
    }

    @WrapOperation(method = "tick", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d fdmc$interpolationVec(double x, double y, double z, Operation<Vec3d> original, @Share("w")LocalDoubleRef w){
        return new Vec4d(x, y, z, w.get());
    }
}
