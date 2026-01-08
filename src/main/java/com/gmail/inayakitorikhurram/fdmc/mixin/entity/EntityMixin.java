package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput {
    public Entity getEntity(){
        return (Entity) (Object) this;
    }
    @Shadow private World world;
    @Shadow public abstract double getX();
    @Shadow public Vec3d pos;
    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract boolean isPlayer();

    @Shadow
    public abstract void move(MovementType type, Vec3d movement);

    @Shadow
    public abstract boolean isLogicalSideForUpdatingMovement();

    @Shadow
    public abstract Entity getRootVehicle();

    @Shadow
    public abstract float getStepHeight();

    @Shadow
    protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    @Shadow
    public abstract void setPosition(Vec3d pos);

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    private @Nullable Entity vehicle;

    @Shadow
    public abstract double getY();

    @Inject(method = "movementInputToVelocity", at = @At(value = "TAIL"), cancellable = true)
    private static void fdmc$movementInput4ToVelocity4(
	    Vec3d movementInput, float speed, float yaw, CallbackInfoReturnable<Vec3d> cir,
        @Local(ordinal = 1) Vec3d speedMovementInput,
        @Local(ordinal = 2) float yawSin,
        @Local(ordinal = 3) float yawCos
    ) {
        Vec4d speed4 = Vec4d.of(speedMovementInput);
        cir.setReturnValue(new Vec4d(
            speed4.x4 * (double)yawCos - speed4.z * (double)yawSin,
            speed4.y,
            speed4.z * (double)yawCos + speed4.x4 * (double)yawSin,
            speed4.w
        ));
    }

    @Inject(
        method = "<init>",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;pos:Lnet/minecraft/util/math/Vec3d;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        )
    )
    void fdmc$setInitialPos4d(EntityType<?> type, World world, CallbackInfo ci) {
        this.pos = Vec4d.ZERO;
    }

    @Redirect(
        method = "setPos",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;"
        )
    )
    Vec3d fdmc$setPos4d(double x, double y, double z) {
        return new Vec4d(x, y, z);
    }

    @ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3d modifyMove(Vec3d movement, @Local(argsOnly = true) MovementType type) {

        if (Vec4d.of(movement).w == 0.0) return movement;
        movement = adjustMovementForSneakingW(movement);
        movement = adjustMovementForCollisionsW(movement);
        Vec4d movement4 = Vec4d.of(movement);

        Vec3d newPos = this.pos.offset(Direction4Constants.ANA, movement4.w);
        this.setPosition(newPos);
        return movement4.flatten();
    }


    @Unique
    private @NotNull Vec3d adjustMovementForSneakingW(Vec3d movement) {
        Vec4d movement4 = Vec4d.of(movement);
        if ((Object) this instanceof PlayerEntity playerEntity) {
            if (playerEntity.isSpaceAroundPlayerEmpty(movement.x, movement.z, this.getStepHeight()) && playerEntity.shouldCancelInteraction()) {
                return movement4.flatten();
            }
        }
        return movement;
    }

    //pretend the player is trying to do a small move in the other slice and then return the result
    @Unique
    private Vec3d adjustMovementForCollisionsW(Vec3d movement) {
        Vec3d originalPos = this.pos;
        Vec4d movement4 = Vec4d.of(movement);
        Vec3d movement4DComponent = new Vec4d(0, 0, 0, movement4.w).toPos3();
        //small shift ignores the zero check
        Vec3d movement3DComponent = new Vec3d(movement4.x4, movement4.y - Math.sqrt(Double.MIN_VALUE), movement4.z);
        Vec3d posWShifted = originalPos.add(movement4DComponent);
        //pretend we've stepped when we do this check
        this.setPosition(posWShifted);
        Vec3d adjusted3DMovement = this.adjustMovementForCollisions(movement3DComponent);
        this.setPosition(originalPos);
        Vec3d adjustedMovementOverall = adjusted3DMovement.add(movement4DComponent);
        return adjustedMovementOverall;
    }
    @WrapMethod(method = "shouldRender(DDD)Z")
    private boolean fdmc$thickRendering(
            double cameraX, double cameraY, double cameraZ,
            Operation<Boolean> original, @Share("dw")LocalDoubleRef dw){
        Vec4d pos4 = Vec4d.of(this.pos);
        Vec4d cameraPos = new Vec4d(cameraX, cameraY, cameraZ);
        Vec3d projectedCameraPos = cameraPos.withAxis(Direction4Constants.Axis4Constants.W, pos4.w).toPos3();
        dw.set(pos4.w - cameraPos.w);
        return original.call(projectedCameraPos.x, projectedCameraPos.y, projectedCameraPos.z);
    }

    @WrapOperation(method = "shouldRender(DDD)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;shouldRender(D)Z"))
    private boolean fdmc$useModifiedRenderDistance(Entity instance, double distance, Operation<Boolean> original,
                                                   @Share("dw") LocalDoubleRef dw){
        return original.call(instance, distance + dw.get() * dw.get() * 16 * 16);
    }

    @ModifyVariable(method = "setMovement(ZLnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3d fdmc$setMovement(Vec3d movement) {
        Vec4d movement4 = Vec4d.of(movement);
        if(movement4.w == 0.0) return movement;
        //Vec3d newPos = this.pos.offset(Direction4Constants.ANA, movement4.w);
        //this.refreshPositionAndAngles(newPos.x, newPos.y, newPos.z, this.getYaw(), this.getPitch());
        return movement4.flatten();
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

    @WrapOperation(
        method = "writeData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/WriteView;put(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
            ordinal = 0
        )
    )
    <T> void fdmc$writePosWithVehicle(WriteView view, String POS_KEY, Codec<T> tCodec, T pos, Operation<Void> original) {
	    assert this.vehicle != null;
	    Vec4d vehiclePos = Vec4d.of(this.vehicle.getEntityPos());
        original.call(view, POS_KEY, Vec4d.CODEC, new Vec4d(
            vehiclePos.getX4(),
            this.getY(),
            vehiclePos.getZ(),
            vehiclePos.getW()
        ));
    }

    @WrapOperation(
        method = "writeData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/WriteView;put(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
            ordinal = 1
        )
    )
    <T> void fdmc$writePosWithoutVehicle(WriteView view, String POS_KEY, Codec<T> tCodec, T pos, Operation<Void> original) {
        original.call(view, POS_KEY, Vec4d.CODEC, pos);
    }

    @WrapOperation(
        method = "readData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/ReadView;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;",
            ordinal = 0
        )
    )
    Optional<Vec4d> fdmc$readPos(ReadView view, String POS_KEY, Codec<Vec3d> tCodec, Operation<Optional<Vec4d>> original) {
        return Optional.of(original.call(view, POS_KEY, Vec4d.CODEC).orElse(Vec4d.ZERO));
    }
}
