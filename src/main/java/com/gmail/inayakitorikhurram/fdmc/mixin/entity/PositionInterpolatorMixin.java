package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PositionInterpolator.class)
public class PositionInterpolatorMixin {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 0))
	double lerpXW(double delta, double startX3, double endX3, @Share("lerpW") LocalDoubleRef lerpW){
		double[] startXW = FDMCMath.splitX3(startX3);
		double[] endXW   = FDMCMath.splitX3(endX3  );

		lerpW.set(MathHelper.lerp(delta, startXW[1], endXW[1]));
		return MathHelper.lerp(delta, startXW[0], endXW[0]);
	}

	@Redirect(method = "tick", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
	Vec3d useLerpW(double lerpX, double lerpY, double lerpZ, @Share("lerpW") LocalDoubleRef lerpW){
		return new Vec4d(lerpX, lerpY, lerpZ, lerpW.get());
	}
}
