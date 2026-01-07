package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity{

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "moveTowardsPlayer", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d fdmc$modifyMovement(double x, double y, double z, Operation<Vec3d> newVec3){

        Vec4d vec4d = Vec4d.of(newVec3.call(x, y, z));

        int dw = (int) vec4d.w;
        if(dw != 0){
            CanStep.of(this).orElseThrow().scheduleStep(dw, false);
            // this could be a separate step but like. no
            if(this.isLogicalSideForUpdatingMovement()) {
                CanStep.of(this).orElseThrow().applyScheduledStep();
            }
        }

        return vec4d.flatten();
    }

}
