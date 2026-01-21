package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnLocating.class)
public class SpawnLocatingMixin {
	@Shadow
	@Final
	private int spawnRadius;

	@Shadow
	@Final
	private BlockPos spawnPos;

	@Unique
	long powerOf2ToPowerOf3(long i) {
		long base = (long) Math.sqrt(i);
		return base * base * base;
	}

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J"))
	long spawnArea4(long upperBound, long spawnArea, Operation<Long> original){
		return original.call(
			powerOf2ToPowerOf3(upperBound),
			powerOf2ToPowerOf3(spawnArea)
		);
	}

	@Definition(id = "j", local = @Local(type = int.class, ordinal = 1))
	@Definition(id = "spawnRadius", field = "Lnet/minecraft/server/network/SpawnLocating;spawnRadius:I")
	@Expression("j % (this.spawnRadius * 2 + 1)")
	@ModifyExpressionValue(method = "scheduleNextSearch", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getNextX4(
		int original, @Local(ordinal = 1) int index,
		@Share("nextX") LocalIntRef x,
		@Share("nextZ") LocalIntRef z,
		@Share("nextW") LocalIntRef w
	){
		int diameter = this.spawnRadius * 2 + 1;

		int x4 = index % diameter;
		int zw = index / diameter;
		assert x4 == original;

		z.set(zw % diameter);
		w.set(zw / diameter);

		x.set(x4);
		return x4;
	}

	@Definition(id = "j", local = @Local(type = int.class, ordinal = 1))
	@Definition(id = "spawnRadius", field = "Lnet/minecraft/server/network/SpawnLocating;spawnRadius:I")
	@Expression("j / (this.spawnRadius * 2 + 1)")
	@ModifyExpressionValue(method = "scheduleNextSearch", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getNextZ(int original, @Share("nextZ") LocalIntRef z){
		return z.get();
	}

	@Definition(id = "spawnPos", field = "Lnet/minecraft/server/network/SpawnLocating;spawnPos:Lnet/minecraft/util/math/BlockPos;")
	@Definition(id = "getX", method = "Lnet/minecraft/util/math/BlockPos;getX()I")
	@Definition(id = "spawnRadius", field = "Lnet/minecraft/server/network/SpawnLocating;spawnRadius:I")
	@Expression("this.spawnPos.getX() + ? - this.spawnRadius")
	@ModifyExpressionValue(method = "scheduleNextSearch", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	int getNextX3(int original, @Share("nextX") LocalIntRef x, @Share("nextW") LocalIntRef w){
		int[] spawnPosXW = FDMCMath.splitX3(this.spawnPos.getX());
		return spawnPosXW[0] + x.get() - this.spawnRadius + FDMCMath.getOffsetX(
		   spawnPosXW[1] + w.get() - this.spawnRadius
		);
	}
}
