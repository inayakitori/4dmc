package com.gmail.inayakitorikhurram.fdmc.mixin.util.math;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vec3d.class)
public class Vec3dMixin {
	@WrapMethod(method = "of")
	private static Vec3d fdmc$ofBlockPos4(Vec3i vec, Operation<Vec3d> original){
		return vec instanceof BlockPos4<?, ?> pos4
			? Vec4d.of(pos4)
			: original.call(vec);
	}
}
