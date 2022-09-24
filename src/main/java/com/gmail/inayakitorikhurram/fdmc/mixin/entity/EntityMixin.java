package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMainEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {

    int stepDirection;

    @Shadow
    private Vec3d velocity;
    public Entity getEntity(){
        return (Entity) (Object) this;
    }

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow public abstract String getEntityName();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    public void afterIsInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(isStepping() && damageSource == DamageSource.IN_WALL) cir.setReturnValue(true);
    }

    //if player is stepping, don't allow movement
    @Inject(method = "getVelocity", at = @At("RETURN"), cancellable = true)
    public void modifiedGetVelocity(CallbackInfoReturnable<Vec3d> cir){
        if(((CanStep)this).isStepping()) {
            cir.setReturnValue(Vec3d.ZERO);
        }
    }

    @Override
    public int getStepDirection() {
        return stepDirection;
    }


    @Override
    public boolean isStepping() {
        return stepDirection == 0;
    }

    @Override
    public void setSteppingLocally(int stepDirection, Vec3d vel) {
        this.stepDirection = stepDirection;
        FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " has " + (isStepping()? "started" : "stopped") + " stepping " + stepDirection);
        if(vel != null) {
            setVelocity(vel);
        }
    }

    @Override
    public void setSteppingGlobally(ServerPlayerEntity player, int stepDirection, Vec3d vel) {
        setSteppingLocally(stepDirection, vel);
        PacketByteBuf bufOut = FDMCMainEntrypoint.writeS2CStepBuffer(stepDirection, vel);
        ServerPlayNetworking.send(player, FDMCConstants.MOVING_PLAYER_ID, bufOut);
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStepping() || this.stepDirection != stepDirection;
    }
}
