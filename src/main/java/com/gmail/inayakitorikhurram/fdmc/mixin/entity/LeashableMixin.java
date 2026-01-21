package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Leashable;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public interface LeashableMixin {
	@Definition(id = "ELASTICITY_MULTIPLIER", field = "Lnet/minecraft/entity/Leashable;ELASTICITY_MULTIPLIER:Lnet/minecraft/util/math/Vec3d;")
	@Definition(id = "Vec3d", type = Vec3d.class)
	@Expression("ELASTICITY_MULTIPLIER = @(new Vec3d(?, ?, ?))")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	static private Vec3d makeStaticVectors4D(Vec3d v){
		return new Vec4d(v.x, v.y, v.z,(v.x + v.z) * 0.5);
	}
}
