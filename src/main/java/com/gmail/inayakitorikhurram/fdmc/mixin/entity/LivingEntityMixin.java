package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.RelativeVec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Entity4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
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
    public float getSidewaysSpeedW() {
        return sidewaysSpeedW;
    }

    @Override
    public void setSidewaysSpeedW(float sidewaysSpeedW) {
        this.sidewaysSpeedW = sidewaysSpeedW;
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
        return new RelativeVec4d(
            sidewaysSpeed,
            upwardSpeed,
            forwardSpeed,
            sidewaysSpeedW
        );
    }

    @Redirect(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"
        )
    )
    void fdmc$tickMovementW(LivingEntity instance, double x, double y, double z, @Local Vec3d velocity){
        double w = RelativeVec4d.of(velocity).w;
        if (this.getType().equals(EntityType.PLAYER)) {
            if (velocity.horizontalLengthSquared() < 9.0E-6) {
                w = 0.0;
            }
        } else {
            if (Math.abs(w) < 0.003) {
                w = 0.0;
            }
        }
        instance.setVelocity(new RelativeVec4d(x, y, z, w));
    }

    @WrapOperation(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    private void fdmc$modifiedSetVelocity(LivingEntity instance, double x, double y, double z, Operation<Void> original, @Local Vec3d vec3d){
        RelativeVec4d relativeVec4d = (RelativeVec4d) vec3d;
        instance.setVelocity(relativeVec4d.withAxis(Direction.Axis.Y, y));
    }

    @Redirect(method = "travelMidAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 0))
    void fdmc$travelMidAirNoDrag4(LivingEntity instance, double x, double y, double z, @Local(ordinal = 1) Vec3d movementInput){
        RelativeVec4d movementInput4 = RelativeVec4d.of(movementInput);
        instance.setVelocity(new RelativeVec4d(movementInput4.x, y, z, movementInput4.w));
    }

    @Redirect(method = "travelMidAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 1))
    void fdmc$travelMidAirDrag4(LivingEntity instance, double x, double y, double z, @Local(ordinal = 1) Vec3d movementInput, @Local(ordinal = 1) float horizontalDrag){
        RelativeVec4d movementInput4 = RelativeVec4d.of(movementInput);
        instance.setVelocity(new RelativeVec4d(movementInput4.x * horizontalDrag, y, movementInput4.z * horizontalDrag, movementInput4.w * horizontalDrag));
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 1))
    Vec3d fdmc$travelGliding1(Vec3d instance, double x, double y, double z, @Local(ordinal = 1) Vec3d rotationVector) {
        double factor = z / rotationVector.z;

        return RelativeVec4d.of(instance).add(x, y, z, sidewaysSpeedW * factor);
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 2))
    Vec3d fdmc$travelGliding2(Vec3d instance, double x, double y, double z, @Local(ordinal = 1) Vec3d rotationVector) {
        double factor = z / rotationVector.z;
        return RelativeVec4d.of(instance).add(x, y, z, sidewaysSpeedW * factor);
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 3))
    Vec3d fdmc$travelGliding3(Vec3d oldVelocity, double x, double y, double z, @Local(ordinal = 1) Vec3d rotationVector) {
        double factor = (z * 10d + oldVelocity.z) / rotationVector.z;
        RelativeVec4d oldVelocity4 = RelativeVec4d.of(oldVelocity);
        return oldVelocity4.add(x, y, z, (sidewaysSpeedW * factor - oldVelocity4.w) * 0.1d);
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d fdmc$travelGliding4(Vec3d oldVelocity, double x, double y, double z) {
        RelativeVec4d oldVelocity4 = RelativeVec4d.of(oldVelocity);
        return oldVelocity4.multiply(x, y, z, (x+z)*.5);
    }

    @Redirect(method = "applyFluidMovingSpeed", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d fdmc$travelInFluid1(double x, double y, double z, @Local(argsOnly = true) Vec3d motion){
        RelativeVec4d motion4 = RelativeVec4d.of(motion);
        return new RelativeVec4d(motion4.x, y, z, motion4.w);
    }

    @Redirect(method = "travelInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 1))
    Vec3d fdmc$travelInFluid2(Vec3d velocity, double x, double y, double z){
        return RelativeVec4d.of(velocity).multiply(x, y, z, (x+z)*.5);
    }

    @Redirect(method = "travelInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    void fdmc$travelInFluid3(LivingEntity instance, double x, double y, double z){
	    instance.setVelocity(RelativeVec4d.of(this.getVelocity()).withAxis(Direction.Axis.Y, y));
    }
}
