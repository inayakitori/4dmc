package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityDimensions.class)
public class EntityDimensionsMixin {
	@Shadow
	@Final
	private float width;

	@Shadow
	@Final
	private float height;

	@WrapMethod(method = "getBoxAt(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Box;")
	Box getBoxAtVec4(Vec3d pos3, Operation<Box> original){
		double radius = (double) this.width * 0.5d;
		Vec4d pos = Vec4d.of(pos3);
		return new Box4(
			pos.x4 - radius,
			pos.y,
			pos.z - radius,
			pos.w - radius,

			pos.x4 + radius,
			pos.y + (double) this.height,
			pos.z + radius,
			pos.w + radius
		);
	}
}
