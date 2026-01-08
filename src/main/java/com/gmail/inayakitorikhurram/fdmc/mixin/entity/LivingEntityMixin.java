package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements SidewaysSpeedW {
    @Unique
    public float sidewaysSpeedW;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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

    @Override
    public void setSidewaysSpeedW(float sidewaysSpeedW) {
        this.sidewaysSpeedW = sidewaysSpeedW;
    }

    @Override
    public float getSidewaysSpeedW() {
        return sidewaysSpeedW;
    }

    @Inject(
        method = "tickMovement",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/LivingEntity;sidewaysSpeed:F",
            shift = At.Shift.AFTER,
            opcode = Opcodes.PUTFIELD
        )
    )
    void fdmc$immobileEntity(CallbackInfo ci){
        setSidewaysSpeedW(0f);
    }

    @Inject(method = "tickMovementInput", at = @At(value = "TAIL"))
    void fdmc$tickMovementInput(CallbackInfo ci) {
        this.sidewaysSpeedW *= 0.98f;
    }

    @Redirect(
        method = "tickMovement",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;"
        )
    )
    Vec3d fdmc$tickMovementW(double sidewaysSpeed, double upwardSpeed, double forwardSpeed){
        return new Vec4d(
            sidewaysSpeed,
            upwardSpeed,
            forwardSpeed,
            sidewaysSpeedW
        );
    }
}
