package com.gmail.inayakitorikhurram.fdmc.mixin.entity.decoration;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractDecorationEntity.class)
public class AbstractDecorationEntityMixin {
	@Redirect(method = "updateAttachmentPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/AbstractDecorationEntity;setPos(DDD)V"))
	void setPos4(AbstractDecorationEntity entity, double x, double y, double z, @Local Vec3d center){
		((Entity4) entity).setPos(Vec4d.of(center));
	}
}
