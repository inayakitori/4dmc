package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.util.TesseroidBlockIterator;
import com.google.common.collect.AbstractIterator;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockCollisionSpliterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisionSpliterator.class)
public abstract class BlockCollisionSpliteratorMixin<T> extends AbstractIterator<T> {
	@Redirect(method = "<init>(Lnet/minecraft/world/CollisionView;Lnet/minecraft/block/ShapeContext;Lnet/minecraft/util/math/Box;ZLjava/util/function/BiFunction;)V", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/util/CuboidBlockIterator;")
	)
	CuboidBlockIterator tesseractIfBoxIs4D(int startX, int startY, int startZ, int endX, int endY, int endZ, @Local(argsOnly = true) Box box){
		return box instanceof Box4 box4
			? new TesseroidBlockIterator(
				startX, startY, startZ, MathHelper.floor(box4.minW - 1E-7) - 1,
				endX, endY, endZ, MathHelper.floor(box4.maxW + 1E-7) + 1
			)
			: new CuboidBlockIterator(startX, startY, startZ, endX, endY, endZ);
	}
}
