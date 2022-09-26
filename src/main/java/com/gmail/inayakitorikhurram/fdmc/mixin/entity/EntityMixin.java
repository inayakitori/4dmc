package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {

    int scheduledStepDirection;
    int stepDirection;
    int stepId;
    boolean ignoreNextStepStartCommand = false;
    SupportHandler supportHandler;
    boolean[] pushableDirections = new boolean[Direction.values().length];

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

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void beforeTick(CallbackInfo ci){
        if(!world.isClient) {
            updatePushableDirectionsGlobally((ServerPlayerEntity)(Object)this);
        }
    }

    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    public void afterIsInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(isStepping() && damageSource == DamageSource.IN_WALL) cir.setReturnValue(true);
    }

    //if player is stepping, then shouldn't be able to move in directions where there are blocks blocking them
    @Inject(method = "getVelocity", at = @At("TAIL"))
    public void modifiedGetVelocity(CallbackInfoReturnable<Vec3d> cir){
        updateVelocity();
    }
    
    private void updateVelocity(){
        if(isStepping()) {
            for(Direction.Axis ax : Direction.Axis.values()){
                double vel = velocity.getComponentAlongAxis(ax);
                Direction dir = null;
                if(vel > 0) {
                    dir = Direction.get(Direction.AxisDirection.POSITIVE, ax);
                } else if(vel < 0) {
                    dir = Direction.get(Direction.AxisDirection.NEGATIVE, ax);
                    vel *= -1;
                }

                if(dir != null && !pushableDirections[dir.getId()] && vel > 0){
                    velocity = velocity.withAxis(ax, 0);
                }

            }
        }
    }

    @Override
    public boolean scheduleStep(int moveDirection) {
        if(!isStepping()) {
            scheduledStepDirection = moveDirection;
            return true;
        } else{
            return false;
        }
    }



    /**TODO write this
    @ Override
    public boolean scheduleStep(int moveDirection) {
        if(((CanStep) player).canStep(moveDirection)) {

            SupportHandler supportHandler = ((CanStep) player).getSupportHandler();

            Vec3d vel = player.getVelocity();
            ((CanStep) player).setSteppingGlobally(player, moveDirection, vel);
            //write to client-side buffer
            Vec3d oldPos = player.getPos();
            Vec3d newPos = oldPos.add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
            //place a block underneath player and clear stone
            supportHandler.queueSupport(UnderSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));
            supportHandler.queueSupport(SuffocationSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));

            //actually tp player
            double[] pos4 = FDMCMath.toPos4(newPos);
            player.teleport(newPos.x, newPos.y, newPos.z);
            player.sendMessage(Text.of(
                    "Moving " + player.getEntityName() + " " + (moveDirection == 1 ? "ana" : "kata") + " to:\n(" +
                            (int) pos4[3] + "," +
                            (int) pos4[0] + "," +
                            (int) pos4[1] + "," +
                            (int) pos4[2] + ")"
            ));
            return true;
        } else{
            return false;
        }
    }
**/

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
                FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " blocked step stepping start " + stepDirection + ", " + stepId % 100);
                this.stepDirection = 0;
                ignoreNextStepStartCommand = false;
                return;
            }
        } else if(this.stepId != stepId){
            this.stepDirection = 0;
            FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " blocked step stepping end, " + stepId % 100);
            //if the player is getting told to stop stepping but it hasn't been told to start, then just ignore this stop stepping and
            ignoreNextStepStartCommand = true;
            return;
        }

        this.stepDirection = stepDirection;
        FDMCConstants.LOGGER.info("Stepping: " + getEntityName() + " has " + (isStepping()? "started"  + " stepping " + stepDirection : "stopped stepping") + ", " + stepId % 100);
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
    public void updatePushableDirectionsLocally(boolean[] pushableDirection) {
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

        if(!wouldCollideAt(blockPos) || !isStepping()){
            Arrays.fill(pushableDirections, true);
            return;
        }
        for(int i = 0; i < 6; i++){
            Direction offsetDirection = Direction.byId(i);
            BlockPos adjacentPos = blockPos.offset(offsetDirection);
            pushableDirections[i] =
                    !wouldCollideAt(adjacentPos) &&
                            (
                                    !wouldCollideAt(adjacentPos, FDMCMath.getOffset(-stepDirection)) ||
                                    isStepping()
                            );
        }

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
