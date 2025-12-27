package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.*;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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


    @Shadow public abstract void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract void updatePosition(double x, double y, double z);

    @Shadow public abstract float getYaw();

    @Shadow public abstract float getPitch();

    @Shadow public abstract void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch);

    @Shadow
    public abstract void move(MovementType type, Vec3d movement);

    @Shadow
    public abstract boolean isLogicalSideForUpdatingMovement();

    @Shadow
    public abstract Entity getRootVehicle();


    @ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3d modifyMove(Vec3d movement) {
        Vec4d movement4 = new Vec4d(movement);
        if(movement4.w == 0.0) return movement;
        Vec3d newPos = this.pos.offset(Direction4Constants.ANA, movement4.w);
        this.refreshPositionAndAngles(newPos.x, newPos.y, newPos.z, this.getYaw(), this.getPitch());
        Vec3d newMovement = new Vec3d(movement4.x, movement4.y, movement4.z);
        return newMovement;
    }
    @ModifyVariable(method = "setMovement(ZLnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3d fdmc$setMovement(Vec3d movement) {
        Vec4d movement4 = new Vec4d(movement);
        if(movement4.w == 0.0) return movement;
        //Vec3d newPos = this.pos.offset(Direction4Constants.ANA, movement4.w);
        //this.refreshPositionAndAngles(newPos.x, newPos.y, newPos.z, this.getYaw(), this.getPitch());
        Vec3d newMovement = new Vec3d(movement4.x, movement4.y, movement4.z);
        return newMovement;
    }


    @Inject(method = "getCameraPosVec", at = @At("RETURN"), cancellable = true)
    private void fdmc$modifyCameraPos(float tickDelta, CallbackInfoReturnable<Vec3d> cir){
        Vec3d val = cir.getReturnValue();
        if((Entity)(Object)this instanceof PlayerEntity player && MixinUtil.shouldShiftInteractionW((player)) && !player.shouldCancelInteraction()) {
            CanPlaceW.of(player).flatMap(CanPlaceW::getPlacementDirection4).ifPresent(direction ->
                    cir.setReturnValue(val.add(direction.getDoubleVector())));
        }
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

    //distance
    @Inject(method = "distanceTo", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceNonSquare(Entity entity, CallbackInfoReturnable<Float> cir){
        double[] xwThis = FDMCMath.splitX3(this.pos.x);
        double[] xwOther = FDMCMath.splitX3(entity.pos.x);
        double dx = xwThis[0] - xwOther[0];
        double dy = this.pos.y - entity.pos.y;
        double dz = this.pos.z - entity.pos.z;
        double dw = xwThis[1] - xwOther[1];
        cir.setReturnValue(MathHelper.sqrt((float) (dx*dx + dy*dy + dz*dz + dw*dw)));
    }

    @Inject(method = "squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceVec3d(Vec3d vector, CallbackInfoReturnable<Double> cir){
        this.modifyDistanceDDD(vector.x, vector.y, vector.z, cir);
    }
    @Inject(method = "squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistanceEntity(Entity entity, CallbackInfoReturnable<Double> cir){
        this.modifyDistanceDDD(entity.pos.x, entity.pos.y, entity.pos.z, cir);
    }


    @Unique
    private int entityScheduledStepDirection = 0;
    private boolean retryOnFail = false;
    private int ticksSinceLastStep = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void incrementTickCount(CallbackInfo ci){
        ticksSinceLastStep++;
    }

    @Override
    public void scheduleStep(int moveDirection, boolean retryOnFail) {
        if(!this.isLogicalSideForUpdatingMovement()) {
            FDMCConstants.LOGGER.warn("LivingEntity({})::scheduleStep of {} called on wrong logical side (client={})", this, entityScheduledStepDirection, this.world.isClient());
        } else if(ticksSinceLastStep > this.stepCooldown()) {
            Entity rootEntity =  this.getRootVehicle();
            if(rootEntity == (Entity) (Object) this) {
                entityScheduledStepDirection = moveDirection;
                this.retryOnFail = retryOnFail;
                //FDMCConstants.LOGGER.info("LivingEntity({})::scheduleStep of {} being set on current logical side (client={})", this, entityScheduledStepDirection, this.world.isClient);
            } else {
                //FDMCConstants.LOGGER.info("LivingEntity({})::scheduleStep of {} being forwarded on current logical side (client={}) (A vehicle)", this, moveDirection, this.world.isClient);
                // TODO this. does not work well. Just disable vehicle stepping for now
                //CanStep.of(rootEntity).ifPresent(canStep -> canStep.scheduleStep(moveDirection));
            }
        }
    }

    @Override
    public void applyScheduledStep() {
        if(entityScheduledStepDirection == 0) return;
        if(!this.isLogicalSideForUpdatingMovement()){
            FDMCConstants.LOGGER.warn("LivingEntity({})::applyScheduledStep of {} called on WRONG logical side (client={})", this, entityScheduledStepDirection, this.world.isClient());
            return;
        }

        boolean successfulMovement = false;

        Vec4d movement4 = Vec4d.of((entityScheduledStepDirection == 1 ? Direction4Constants.ANA4 : Direction4Constants.KATA4).getVector4());
        int w = (int) FDMCMath.splitX3(this.pos.offset(Direction4Constants.ANA, entityScheduledStepDirection).x)[1];
        int w_max = (int) Math.floor(FDMCConstants.MAX_SLICE / this.world.getDimension().coordinateScale());

        //Box4 offsetPos = Box4.converted(this.getBoundingBox()).offset(0, 0, 0, entityScheduledStepDirection);
        Box offsetPos = this.getBoundingBox().offset(movement4.toPos3());
        if(this.world.isBlockSpaceEmpty((Entity)(Object) this, offsetPos)) {
            if(Math.abs(w) <= w_max) {
                //FDMCConstants.LOGGER.info("LivingEntity({})::applyScheduledStep of {} on logical side (client={})", this, entityScheduledStepDirection, this.world.isClient());
                this.move(MovementType.SELF, movement4.toPos3());
                successfulMovement = true;
            } else {
                FDMCConstants.LOGGER.info("LivingEntity({})::applyScheduledStep of {} on logical side (client={}) SKIPPED because position is out of this world", this, entityScheduledStepDirection, this.world.isClient());
            }
        } else {
            //FDMCConstants.LOGGER.info("LivingEntity({})::applyScheduledStep of {} skipped due to collision on logical side (client={})", this, entityScheduledStepDirection, this.world.isClient());
        }

        if(successfulMovement || !retryOnFail) {
            entityScheduledStepDirection = 0;
            ticksSinceLastStep = 0;
        }
    }

    @Override
    public int stepCooldown(){
        return 1;
    }

    @Override
    public int getCurrentStepDirection() {
        return entityScheduledStepDirection;
    }

}
