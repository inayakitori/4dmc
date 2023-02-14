package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.*;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {

    int scheduledStepDirection;
    int stepDirection;
    int stepId;
    boolean ignoreNextStepStartCommand = false;
    SupportHandler supportHandler;
    boolean[] pushableDirections = new boolean[Direction.values().length];
    Direction placementDirection4 = null;

    @Override
    public void setPlacementDirection4(Direction placementDirection4) {
        this.placementDirection4 = placementDirection4;
    }

    @Override
    public Optional<Direction> getPlacementDirection4() {
        return Optional.ofNullable(placementDirection4);
    }

    @Shadow
    private Vec3d velocity;
    
    public Entity getEntity(){
        return (Entity) (Object) this;
    }


    @Shadow public abstract String getEntityName();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public int age;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityType<?> type, World world, CallbackInfo ci){
        supportHandler = new SupportHandler();
    }

    // If the client gets a stop stepping command with an unknown id, then the stop command has arrived first and so
    // both the start and stop commands should be ignored; the player has already finished the stepping on the serverside
    @Inject(method = "baseTick", at = @At("HEAD"))
    public void baseTick(CallbackInfo ci) {
        supportHandler.tickSupports();
        if (scheduledStepDirection != 0) {
            step(scheduledStepDirection);
            scheduledStepDirection = 0;
        }
    }

    @Shadow public World world;

    @Shadow private BlockPos blockPos;

    @Shadow public abstract void updateVelocity(float speed, Vec3d movementInput);

    @Shadow public abstract double getX();

    @Shadow private Vec3d pos;

    @Shadow private Box boundingBox;

    @Shadow public abstract Box getBoundingBox();

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void beforeTick(CallbackInfo ci){
        if(!world.isClient && isStepping()) {
            updatePushableDirectionsGlobally((ServerPlayerEntity)(Object)this);
        } else if(!isStepping()){
            Arrays.fill(pushableDirections, true);
        }
    }

    //cancel suffocation
    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    public void afterIsInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(isStepping() && damageSource == DamageSource.IN_WALL) cir.setReturnValue(true);
    }

    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At("TAIL"))
    public void modifiedSetVelocity(Vec3d velocity, CallbackInfo ci){
        updateVelocity();
    }

    //if player is stepping, then shouldn't be able to move in directions where there are blocks blocking them
    @Inject(method = "getVelocity", at = @At("TAIL"))
    public void modifiedGetVelocity(CallbackInfoReturnable<Vec3d> cir){
        updateVelocity();
    }

    //TODO this is really bad code :/
    private void updateVelocity(){
        if(isStepping() && doesCollideWithBlocksAt(blockPos)) {
            Vec3d inBlockPos = pos.subtract(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            for(Direction.Axis ax : Direction.Axis.values()){
                double velAx = velocity.getComponentAlongAxis(ax);
                double newVelAx;
                if(ax.equals(Direction.Axis.Y)){
                    if(!pushableDirections[Direction.DOWN.getId()]){
                        newVelAx = Math.max(0, velAx);
                        velocity = velocity.withAxis(ax, newVelAx);
                    }
                    continue;
                }
                double inBlockAx = inBlockPos.getComponentAlongAxis(ax);
                Direction outDirAx;
                int outOffsetAx;
                if(inBlockAx > 0.1){ //on +ve side
                    outDirAx = Direction.get(Direction.AxisDirection.POSITIVE, ax);
                } else if(inBlockAx < -0.1){//on -ve side
                    outDirAx = Direction.get(Direction.AxisDirection.NEGATIVE, ax);
                } else{ //in the middle, can go both ways so try both
                    outDirAx = Direction.get(Direction.AxisDirection.POSITIVE, ax);
                    if(!pushableDirections[outDirAx.getId()]){ //if can't go +ve, try negative
                        outDirAx = Direction.get(Direction.AxisDirection.NEGATIVE, ax);
                        if(!pushableDirections[outDirAx.getId()]) { //also can't go +ve, just keep in middle
                            velocity = velocity.withAxis(ax, 0);
                            continue;
                        }
                    }
                }

                outOffsetAx = outDirAx.getDirection().offset();

                if(pushableDirections[outDirAx.getId()]){ //can be pushed out
                    newVelAx = outOffsetAx * MathHelper.absMax(0.1, velAx);
                } else{//cannot be pushed out, must be pulled in
                    newVelAx = outOffsetAx * -0.1;
                }
                velocity = velocity.withAxis(ax, newVelAx);

            }
        }
    }

    @Override
    public boolean scheduleStep(int stepDirection) {
        if(canStep(stepDirection)) {
            scheduledStepDirection = stepDirection;
            return true;
        } else{
            return false;
        }
    }

    @Override
    public int getStepDirection() {
        return stepDirection;
    }
    @Override
    public SupportHandler getSupportHandler() {
        return supportHandler;
    }

    @Override
    public boolean isStepping() {
        return stepDirection != 0;
    }

    @Override
    public void setSteppingLocally(int stepId, int stepDirection, Vec3d vel) {
        if(stepDirection != 0){
            if(!ignoreNextStepStartCommand){
                this.stepId = stepId;
            } else{
                //if the player is being told to start stepping but they've already been told to stop stepping with the same step ID, just ignore it once
                //FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " blocked step stepping start " + stepDirection + ", " + stepId % 100);
                this.stepDirection = 0;
                ignoreNextStepStartCommand = false;
                return;
            }
        } else if(this.stepId != stepId){
            this.stepDirection = 0;
            //FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " blocked step stepping end, " + stepId % 100);
            //if the player is getting told to stop stepping but it hasn't been told to start, then just ignore this stop stepping and
            ignoreNextStepStartCommand = true;
            return;
        }

        this.stepDirection = stepDirection;
        //FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " has " + (isStepping()? "started"  + " stepping " + stepDirection : "stopped stepping") + ", " + stepId % 100);
        if(vel != null) {
            setVelocity(vel);
        }
    }
    @Override
    public void setSteppingGlobally(ServerPlayerEntity player, int stepDirection, Vec3d vel) {
        //if the player is stopping a step, use the current stepping id, otherwise create a new one from the entity's age
        int stepId = stepDirection == 0? this.stepId : player.age;
        setSteppingLocally(stepId, stepDirection, vel);
        PacketByteBuf bufOut = writeS2CStepBuffer(stepId, stepDirection, vel);
        ServerPlayNetworking.send(player, FDMCConstants.MOVING_PLAYER_ID, bufOut);
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStepping() || this.stepDirection != stepDirection;
    }

    @Override
    public void updatePushableDirectionsLocally(boolean[] pushableDirections) {
        this.pushableDirections = Arrays.copyOf(pushableDirections, pushableDirections.length);
    }

    @Override
    public void updatePushableDirectionsGlobally(ServerPlayerEntity player) {
        calculatePushableDirections();
        if(player != null) {
            PacketByteBuf bufOut = writeS2CPushBuffer(pushableDirections);
            ServerPlayNetworking.send(player, FDMCConstants.UPDATE_COLLISION_MOVEMENT, bufOut);
        }
    }

    //check each direction and it's stepped equivalent
    private void calculatePushableDirections(){



        if(!doesCollideWithBlocksAt(blockPos) || !isStepping()){
            Arrays.fill(pushableDirections, true);
            return;
        }
        for(int i = 0; i < 6; i++){
            Direction offsetDirection = Direction.byId(i);
            BlockPos adjacentPos = blockPos.offset(offsetDirection);
            pushableDirections[i] =
                    !doesCollideWithBlocksAt(adjacentPos) && // can't collide
                    !world.getBlockState(adjacentPos.offset(Direction.DOWN)).isAir() && //can;t fall
                            (
                                    !doesCollideWithBlocksAt(adjacentPos.add(FDMCMath.getOffset(-stepDirection))) || //can't collide in direction stepped from
                                            !isStepping()
                            );
        }

    }

    @Override
    public boolean doesCollideWithBlocksAt(BlockPos pos) {
        return doesCollideWithBlocksAt(pos.subtract(blockPos), true);
    }
    @Override
    public boolean doesCollideWithBlocksAt(BlockPos offset, boolean fromOffset) {
        BlockPos offsetPlayerPos = blockPos.add(offset);
        Box box = this.getBoundingBox().offset(offset);
        Box box2 = new Box(offsetPlayerPos.getX(), box.minY, offsetPlayerPos.getZ(), (double)offsetPlayerPos.getX() + 1.0, box.maxY, (double)offsetPlayerPos.getZ() + 1.0).contract(1.0E-7);
        return !world.isSpaceEmpty(box2);
    }

    private static PacketByteBuf writeS2CPushBuffer(boolean[] pushableDirections){
        PacketByteBuf bufOut = PacketByteBufs.create();
        for(boolean pushableDirection: pushableDirections) {
            bufOut.writeBoolean(pushableDirection);
        }
        return bufOut;
    }

    private static PacketByteBuf writeS2CStepBuffer(int tick, int stepDirection, Vec3d vel){
        PacketByteBuf bufOut = PacketByteBufs.create();
        bufOut.writeInt(tick);
        bufOut.writeInt(stepDirection);
        if(vel!=null) {
            bufOut.writeDouble(vel.x);
            bufOut.writeDouble(vel.y);
            bufOut.writeDouble(vel.z);
        }

        return bufOut;
    }
}
