package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CanStep {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = {
            "travelFlying(Lnet/minecraft/util/math/Vec3d;FFF)V"
    }, at = @At("HEAD"))
    private void fdmc$travelFlying4(Vec3d movementInput, float inWaterSpeed, float inLavaSpeed, float regularSpeed, CallbackInfo ci){
        if(this.isLogicalSideForUpdatingMovement()) {
            applyScheduledStep();
        }
    }

    @Inject(method = {
            "travel",
    }, at = @At("HEAD"))
    private void fdmc$travel4(Vec3d movementInput, CallbackInfo ci){
        if(this.isLogicalSideForUpdatingMovement()) {
            applyScheduledStep();
        }
    }

}
