package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CanStep {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE))
    private void fdmc$travel4(CallbackInfo ci){
        if(this.isLogicalSideForUpdatingMovement()) {
            applyScheduledStep();
        }
    }

    @WrapMethod(method = "canSee(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/RaycastContext$ShapeType;Lnet/minecraft/world/RaycastContext$FluidHandling;D)Z")
    private boolean fdmc$modifiedSightCheck(Entity entity, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, double entityY, Operation<Boolean> original){
        Vec3d originalPos3 = this.pos;
        for(int dw = -FDMCConstants.RAYCAST_THICKNESS; dw <= FDMCConstants.RAYCAST_THICKNESS; dw++) {
            this.pos = originalPos3.add(Direction4Constants.ANA.getDoubleVector().multiply(dw));
            if(original.call(entity, shapeType, fluidHandling, entityY)){
                this.pos = originalPos3;
                return true;
            }
        }

        this.pos = originalPos3;
        return false;
    }


}
