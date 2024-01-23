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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {
    public Entity getEntity(){
        return (Entity) (Object) this;
    }
    @Shadow private World world;
    @Shadow public abstract double getX();
    @Shadow public Vec3d pos;
    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract Vec3d getPos();

    @Shadow public abstract void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract void updatePosition(double x, double y, double z);

    @Shadow public abstract float getYaw();

    @Shadow public abstract float getPitch();

    @Shadow public abstract void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch);

    int entityScheduledStepDirection;
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
    }
    @ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3d modifyMove(Vec3d movement) {
        Vec4d movement4 = new Vec4d(movement);
        if(movement4.w == 0.0) return movement;
        Vec3d newPos = this.pos.offset(Direction4Constants.ANA, movement4.w);
        this.refreshPositionAndAngles(newPos.x, newPos.y, newPos.z, this.getYaw(), this.getPitch());
        Vec3d newMovement = new Vec3d(movement4.x, movement4.y, movement4.z);
        return newMovement;
    }

    //distance
    @Inject(method = "squaredDistanceTo(DDD)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceDDD(double x, double y, double z, CallbackInfoReturnable<Double> cir){
        double[] xwThis = FDMCMath.splitX3(this.pos.x);
        double[] xwOther = FDMCMath.splitX3(x);
        double dx = xwThis[0] - xwOther[0];
        double dy = this.pos.y - y;
        double dz = this.pos.z - z;
        double dw = xwThis[1] - xwOther[1];
        cir.setReturnValue(dx*dx + dy*dy + dz*dz + dw*dw);
    }
    @Inject(method = "squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceVec3d(Vec3d vector, CallbackInfoReturnable<Double> cir){
        this.modifyDistanceDDD(vector.x, vector.y, vector.z, cir);
    }
    @Inject(method = "squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceEntity(Entity entity, CallbackInfoReturnable<Double> cir){
        this.modifyDistanceDDD(entity.pos.x, entity.pos.y, entity.pos.z, cir);
    }

    @Override
    public int getCurrentStepDirection() {
        return entityScheduledStepDirection;
    }

    @Override
    public boolean canStep(int stepDirection) {
        return true;
    }

}
