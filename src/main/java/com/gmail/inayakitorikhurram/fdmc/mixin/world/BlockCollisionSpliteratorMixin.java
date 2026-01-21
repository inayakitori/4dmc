package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.util.TesseroidBlockIterator;
import com.google.common.collect.AbstractIterator;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockCollisionSpliterator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisionSpliterator.class)
public abstract class BlockCollisionSpliteratorMixin<T> extends AbstractIterator<T> {
	@Shadow
	@Final
	private CuboidBlockIterator blockIterator;

	@Shadow
	@Final
	private Box box;

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

	@Redirect(method = "computeNext", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/math/Box;intersects(DDDDDD)Z"
	))
	boolean intersects4(Box box, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (this.blockIterator instanceof TesseroidBlockIterator iterator4 && this.box instanceof Box4 box4) {
			double x = iterator4.getX4(), w = iterator4.getW();
			return box4.intersects(
				x, minY, minZ, w,
				x+1, maxY, maxZ, w+1
			);
		}
		return box.intersects(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
