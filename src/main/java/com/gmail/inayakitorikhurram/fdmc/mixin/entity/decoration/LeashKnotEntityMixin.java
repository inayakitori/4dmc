package com.gmail.inayakitorikhurram.fdmc.mixin.entity.decoration;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeashKnotEntity.class)
public abstract class LeashKnotEntityMixin extends BlockAttachedEntity {
	protected LeashKnotEntityMixin(EntityType<? extends BlockAttachedEntity> entityType, World world) {
		super(entityType, world);
	}

	@Redirect(method = "updateAttachmentPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/LeashKnotEntity;setPos(DDD)V"))
	void setPos4(LeashKnotEntity entity, double x, double y, double z){
		BlockPos4<?, ?> pos = BlockPos4.of(this.attachedBlockPos);
		((Entity4) entity).setPos(new Vec4d(
			(double) pos.getX4() + 0.5,
			(double) pos.getY4() + 0.375,
			(double) pos.getZ4() + 0.5,
			(double) pos.getW4() + 0.5
		));
	}

	@Redirect(method = "updateAttachmentPosition", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/util/math/Box;"))
	Box setBoundingBox4(double x1, double y1, double z1, double x2, double y2, double z2, @Local(ordinal = 0) double d, @Local(ordinal = 1) double e){
		Vec4d pos = Vec4d.of(this.getEntityPos());
		return new Box4(
			pos.x4 - d,
			pos.y,
			pos.z - d,
			pos.w - d,

			pos.x4 + d,
			pos.y + e,
			pos.z + d,
			pos.w + d
		);
	}
}
