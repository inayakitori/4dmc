package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {

    int stepDirection;
    boolean isStepping;

    boolean[] movableDirections = new boolean[Direction.values().length];

    @Shadow
    private Vec3d velocity;
    public Entity getEntity(){
        return (Entity) (Object) this;
    }

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow public abstract String getEntityName();



    @Shadow public World world;

    @Shadow private BlockPos blockPos;

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void beforeTick(CallbackInfo ci){
        if(!world.isClient) {
            updateMoveDirections();
        }
    }

    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    public void afterIsInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(isStepping && damageSource == DamageSource.IN_WALL) cir.setReturnValue(true);
    }

    //if player is stepping, then shouldn't be able to move in directions where there are blocks blocking them
    @Inject(method = "getVelocity", at = @At("TAIL"))
    public void modifiedGetVelocity(CallbackInfoReturnable<Vec3d> cir){
        updateVelocity();
    }
    
    private void updateVelocity(){
        if(isStepping) {
            for(Direction.Axis ax : Direction.Axis.values()){
                double vel = velocity.getComponentAlongAxis(ax);
                Direction dir = null;
                if(vel > 0) {
                    dir = Direction.get(Direction.AxisDirection.POSITIVE, ax);
                } else if(vel < 0) {
                    dir = Direction.get(Direction.AxisDirection.NEGATIVE, ax);
                    vel *= -1;
                }

                if(dir != null && !movableDirections[dir.getId()] && vel > 0){
                    velocity = velocity.withAxis(ax, 0);
                }

            }
        }
    }

    @Override
    public int getStepDirection() {
        return stepDirection;
    }

    @Override
    public void setStepDirection(int stepDirection) {
        this.stepDirection = stepDirection;
    }

    @Override
    public boolean isStepping() {
        return isStepping;
    }

    @Override
    public void setStepping(boolean isStepping) {
        this.isStepping = isStepping && stepDirection != 0;
        FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " has " + (isStepping? "started" : "stopped") + " stepping " + stepDirection);
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStepping || this.stepDirection != stepDirection;
    }

    //check each direction and it's stepped equivalent
    @Override
    public void updateMoveDirections(){
        if(!wouldCollideAt(blockPos) || !isStepping){
            Arrays.fill(movableDirections, true);
            return;
        }
        for(int i = 0; i < 6; i++){
            Direction offsetDirection = Direction.byId(i);
            BlockPos adjacentPos = blockPos.offset(offsetDirection);
            movableDirections[i] =
                    !wouldCollideAt(adjacentPos) &&
                            (
                                    !wouldCollideAt(adjacentPos, FDMCMath.getOffset(-stepDirection)) ||
                                    isStepping
                            );
        }
    }
    @Override
    public boolean[] getMoveDirections() {
        return movableDirections;
    }


    private boolean wouldCollideAt(BlockPos pos) {
        return wouldCollideAt(pos, BlockPos.ORIGIN);
    }

    private boolean wouldCollideAt(BlockPos currentPlayerPos, BlockPos offset) {
        BlockPos offsetPlayerPos = currentPlayerPos.add(offset);
        Box box = this.getBoundingBox().offset(offset);
        Box box2 = new Box(offsetPlayerPos.getX(), box.minY, offsetPlayerPos.getZ(), (double)offsetPlayerPos.getX() + 1.0, box.maxY, (double)offsetPlayerPos.getZ() + 1.0).contract(1.0E-7);
        return world.canCollide(null, box2);
    }

}
