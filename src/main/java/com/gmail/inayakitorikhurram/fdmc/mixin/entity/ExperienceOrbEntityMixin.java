package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity{

    @Shadow
    private @Nullable PlayerEntity target;

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "moveTowardsPlayer", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d fdmc$modifyMovement(double x, double y, double z, Operation<Vec3d> newVec3){
	    assert this.target != null;
	    Vec4d targetPos = Vec4d.of(this.target.getEntityPos());
        Vec4d thisPos = Vec4d.of(this.getEntityPos());
        return new Vec4d(targetPos.x4 - thisPos.x4, y, z, targetPos.w - thisPos.w);
    }

}
