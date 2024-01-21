package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.*;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {
    @Shadow
    private Vec3d velocity;
    public Entity getEntity(){
        return (Entity) (Object) this;
    }
    @Shadow public abstract String getEntityName();
    @Shadow public abstract void setVelocity(Vec3d velocity);
    @Shadow public int age;
    @Shadow @Final private static Logger LOGGER;
    @Shadow public World world;
    @Shadow private BlockPos blockPos;
    @Shadow public abstract void updateVelocity(float speed, Vec3d movementInput);
    @Shadow public abstract double getX();
    @Shadow private Vec3d pos;
    @Shadow private Box boundingBox;
    @Shadow public abstract Box getBoundingBox();
    @Shadow public abstract void setPosition(Vec3d pos);

    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract Vec3d getPos();

    @Shadow public abstract void resetPosition();

    @Shadow public abstract void requestTeleport(double destX, double destY, double destZ);

    int entityScheduledStepDirection;
    boolean isStillStepping;
    boolean[] pushableDirections = new boolean[Direction.values().length];
    Optional<Direction> placementDirection4 = Optional.empty();
    @Override
    public void setPlacementDirection4(Direction placementDirection4) {
        this.placementDirection4 = Optional.of(placementDirection4);
    }
    @Override
    public void setPlacementDirection4(@NotNull Optional<Direction> placementDirection4) {
        this.placementDirection4 = placementDirection4;
    }
    @Override
    public Optional<Direction> getPlacementDirection4() {
        return placementDirection4;
    }

    @Override
    public void scheduleStep(int moveDirection) {
        this.entityScheduledStepDirection = moveDirection;
        this.setStillStepping(moveDirection!=0);
    }

    // If the client gets a stop stepping command with an unknown id, then the stop command has arrived first and so
    // both the start and stop commands should be ignored; the player has already finished the stepping on the serverside
    @Inject(method = "move", at = @At("HEAD"))
    public void modifyMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (entityScheduledStepDirection != 0) {
            Vec3d newPos = this.getPos().offset(Direction4Constants.ANA, entityScheduledStepDirection + 0.);
            this.requestTeleport(newPos.x, newPos.y, newPos.z);
            this.resetPosition();
            entityScheduledStepDirection = 0;
        } else {
            if(this.isStillStepping){
                this.setStillStepping(false);
                if (((Entity)(Object)this) instanceof ServerPlayerEntity serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, FDMCConstants.MOVING_PLAYER_ID, PacketByteBufs.create());
                }
            }
        }
    }

    //distance
    @Inject(method = "squaredDistanceTo(DDD)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceDDD(double x, double y, double z, CallbackInfoReturnable<Double> cir){
        Vec4d other = new Vec4d(x, y, z);
        Vec4d thisPos = new Vec4d(pos);
        cir.setReturnValue(squaredDistanceBetween(thisPos, other));
        cir.cancel();
    }
    @Inject(method = "squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceVec3d(Vec3d vector, CallbackInfoReturnable<Double> cir){
        Vec4d other = new Vec4d(vector);
        Vec4d thisPos = new Vec4d(pos);
        cir.setReturnValue(squaredDistanceBetween(thisPos, other));
        cir.cancel();
    }
    @Inject(method = "squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceEntity(Entity entity, CallbackInfoReturnable<Double> cir){
        Vec4d other = new Vec4d(entity.getPos());
        Vec4d thisPos = new Vec4d(pos);
        cir.setReturnValue(squaredDistanceBetween(thisPos, other));
        cir.cancel();
    }

    private static double squaredDistanceBetween(Vec4d v1, Vec4d v2){
        double dx = v1.getX() - v2.getX();
        double dy = v1.getY() - v2.getY();
        double dz = v1.getZ() - v2.getZ();
        double dw = v1.getW() - v2.getW();
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }

    @Override
    public int getCurrentStepDirection() {
        return entityScheduledStepDirection;
    }
    @Override
    public boolean isStillStepping() {
        return this.isStillStepping;
    }

    @Override
    public void setStillStepping(boolean val) {
        this.isStillStepping = val;
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStillStepping() || this.entityScheduledStepDirection != stepDirection;
    }

}
