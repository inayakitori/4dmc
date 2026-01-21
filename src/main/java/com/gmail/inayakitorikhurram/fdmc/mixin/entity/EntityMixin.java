package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.*;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Entity4;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, Entity4 {
    @Unique
    public Entity getEntity(){
        return (Entity) (Object) this;
    }
    @Shadow private World world;
    @Shadow public abstract double getX();
    @Shadow public Vec3d pos;
    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract boolean isPlayer();

    @Shadow
    private @Nullable Entity vehicle;

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract Vec3d getEntityPos();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public BlockPos blockPos;

    @Shadow
    private @Nullable BlockState stateAtPos;

    @Shadow
    private ChunkPos chunkPos;

    @Shadow
    private EntityChangeListener changeListener;

    @Shadow
    protected boolean firstUpdate;

    @Shadow
    public abstract void setBoundingBox(Box boundingBox);

    @Shadow
    protected abstract Box calculateBoundingBox();

    @Shadow
    public abstract void setAngles(float yaw, float pitch);

    @Shadow
    public double lastX;

    @Shadow
    public double lastY;

    @Shadow
    public double lastZ;

    @Shadow
    protected abstract double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor);

    @Mutable
    @Shadow
    @Final
    private double[] pistonMovementDelta;

    @Shadow
    public abstract void setYaw(float yaw);

    @Shadow
    public abstract void setPitch(float pitch);

    @Shadow
    public abstract void resetPosition();

    @Shadow
    protected abstract void refreshPosition();

    @Inject(method = "<init>", at = @At("TAIL"))
    void construct4(EntityType<?> type, World world, CallbackInfo ci) {
        pistonMovementDelta = new double[]{0,0,0,0};
    }

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

    @Redirect(
        method = "setPos",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;"
        )
    )
    Vec3d fdmc$setPos4d(double x, double y, double z) {
        FDMCConstants.LOGGER.debug("Something tried to set position with a 3D vector. The caller should be patched with mixins.\n{}", ExceptionUtils.getStackTrace(new Throwable()));
        return new Vec4d(x, y, z);
    }

    @Redirect(
        method = "setPosition(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;setPosition(DDD)V"
        )
    )
    void fdmc$setPositionVec4d(Entity instance, double x, double y, double z, @Local(argsOnly = true) Vec3d vec3) {
        Vec4d vec = Vec4d.of(vec3);
        ((Entity4) instance).setPosition(vec);
    }

    @WrapMethod(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    void fdmc$setVelocity4d(Vec3d velocity, Operation<Void> original) {
        if (!(velocity instanceof Vec4d)) {
	        FDMCConstants.LOGGER.debug("Something tried to set velocity with a 3D vector. The caller should be patched with mixins.\n{}", ExceptionUtils.getStackTrace(new Throwable()));
        }
        // Enforce that velocity is always set to 4D
        original.call(Vec4d.of(velocity));
    }

    @Override
    public void setPosition(Vec4d position) {
        this.setPos(position);
        this.setBoundingBox(this.calculateBoundingBox());
    }

    // Is there a simpler way to add an extra argument to a method? I don't think so :C
    @Override
    public final void setPos(Vec4d newPos) {
        Vec4d pos = Vec4d.of(this.getEntityPos());
        if (pos.x4 != newPos.x4 || pos.y != newPos.y || pos.z != newPos.z || pos.w != newPos.w) {
            BlockPos4<?, ?> blockPos4 = BlockPos4.of(this.getBlockPos());
            this.pos = newPos;
            int fx = MathHelper.floor(newPos.x4);
            int fy = MathHelper.floor(newPos.y);
            int fz = MathHelper.floor(newPos.z);
            int fw = MathHelper.floor(newPos.w);
            if (fx != blockPos4.getX4() || fy != blockPos4.getY4() || fz != blockPos4.getZ4() || fw != blockPos4.getW4()) {
                this.blockPos = BlockPos4.newBlockPos4(fx, fy, fz, fw).asBlockPos();
                this.stateAtPos = null;
                if (ChunkSectionPos.getSectionCoord(fx * FDMCMath.getOffsetX(fw)) != this.chunkPos.x || ChunkSectionPos.getSectionCoord(fz) != this.chunkPos.z) {
                    this.chunkPos = new ChunkPos(this.blockPos);
                }
            }
            this.changeListener.updateEntityPosition();
            if (!this.firstUpdate && this.world instanceof ServerWorld serverWorld) {
                if (!this.isRemoved()) {
                    Entity entity = this.getEntity();
                    if (entity instanceof ServerWaypoint serverWaypoint && serverWaypoint.hasWaypoint()) {
                        serverWorld.getWaypointHandler().onUpdate(serverWaypoint);
                    }
                    if (entity instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.canReceiveWaypoints() && serverPlayerEntity.networkHandler != null) {
                        serverWorld.getWaypointHandler().updatePlayerPos(serverPlayerEntity);
                    }
                }
            }
        }
    }

    @Override
    public void updatePosition(Vec4d position) {
        double clampX3 = MathHelper.clamp(position.x , -3.0E7, 3.0E7);
        double clampX4 = MathHelper.clamp(position.x4, -3.0E7, 3.0E7);
        double clampZ  = MathHelper.clamp(position.z , -3.0E7, 3.0E7);
        double clampW  = MathHelper.clamp(position.w , -3.0E7, 3.0E7);
        this.lastX = clampX3;
        this.lastY = position.y;
        this.lastZ = clampZ;
        this.setPosition(new Vec4d(clampX4, position.y, clampZ, clampW));
    }

    @Override
    public void updatePositionAndAngles(Vec4d position, float yaw, float pitch) {
        this.updatePosition(position);
        this.setAngles(yaw, pitch);
    }

    @Override
    public void refreshPositionAndAngles(Vec4d position, float yaw, float pitch) {
        this.setPos(position);
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.resetPosition();
        this.refreshPosition();
    }

    @Redirect(method = "refreshPositionAndAngles(Lnet/minecraft/util/math/Vec3d;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V"))
    void refreshPositionAndAnglesVec4(Entity instance, double x, double y, double z, float yaw, float pitch, @Local(argsOnly = true) Vec3d vec){
        this.refreshPositionAndAngles(Vec4d.of(vec), yaw, pitch);
    }

    @Redirect(method = "refreshPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPosition(DDD)V"))
    void refreshPosition4(Entity instance, double x, double y, double z) {
        this.setPosition(Vec4d.of(this.getEntityPos()));
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d fdmc$doNotResetVelocityW(Vec3d instance, double x, double y, double z) {
        return Vec4d.of(instance).multiply(x, y, z, (x+z)*.5d);
    }

    @Expression("?*? + ?*? + ?*?")
    @ModifyExpressionValue(method = "shouldRender(DDD)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double fdmc$thickRendering(
        double original,
        @Local(argsOnly = true, ordinal = 0) double cameraX,
        @Local(argsOnly = true, ordinal = 1) double cameraY,
        @Local(argsOnly = true, ordinal = 2) double cameraZ
    ){
        return Vec4d.of(this.pos).subtract(new Vec4d(cameraX, cameraY, cameraZ)).lengthSquared();
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

    @Definition(id = "approximatelyEquals", method = "Lnet/minecraft/util/math/MathHelper;approximatelyEquals(DD)Z")
    @Definition(id = "x", field = "Lnet/minecraft/util/math/Vec3d;x:D")
    @Expression("approximatelyEquals(?.x, @(?.x))")
    @ModifyReceiver(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
    Vec3d saveAdjustedForCollisionsMovement(Vec3d adjustedForCollisionsMovement, @Share("adjustedForCollisionsMovement") LocalRef<Vec4d> ref) {
        ref.set(Vec4d.of(adjustedForCollisionsMovement));
        return adjustedForCollisionsMovement;
    }

    @Definition(id = "approximatelyEquals", method = "Lnet/minecraft/util/math/MathHelper;approximatelyEquals(DD)Z")
    @Definition(id = "x", field = "Lnet/minecraft/util/math/Vec3d;x:D")
    @Expression("approximatelyEquals(?.x, ?.x)")
    @WrapOperation(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
    boolean checkHorizontalCollisionsAtW(
        double beforeX3, double afterX3, Operation<Boolean> original,
        @Local(argsOnly = true) Vec3d movement,
        @Share("adjustedForCollisionsMovement") LocalRef<Vec4d> ref
    ){
        Vec4d movement4 = Vec4d.of(movement);
        Vec4d adjustedForCollisionsMovement = ref.get();
        return original.call(movement4.x4, adjustedForCollisionsMovement.x4)
            && original.call(movement4.w, adjustedForCollisionsMovement.w);
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

    @ModifyArg(
        method = "writeData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/WriteView;put(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
            ordinal = 1
        ),
        index = 1
    )
    Codec<?> fdmc$writePosWithoutVehicle(Codec<?> codec) {
        return Vec4d.CODEC;
    }

    @ModifyArg(
        method = "readData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/ReadView;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;",
            ordinal = 0
        ),
        index = 1
    )
    Codec<?> fdmc$readPos(Codec<?> codec) {
        return Vec4d.CODEC;
    }

    @ModifyArg(
        method = "writeData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/WriteView;put(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
            ordinal = 2
        ),
        index = 1
    )
    Codec<?> fdmc$writeVelocity(Codec<?> codec) {
        return Vec4d.CODEC;
    }

    @ModifyArg(
        method = "readData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/storage/ReadView;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;",
            ordinal = 1
        ),
        index = 1
    )
    Codec<?> fdmc$readVelocity(Codec<?> var2) {
        return Vec4d.CODEC;
    }

    @Redirect(method = "addVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    void useAddVelocityInternal(Entity instance, double deltaX, double deltaY, double deltaZ, @Local(argsOnly = true) Vec3d dV) {
        instance.addVelocityInternal(dV);
    }

    @Redirect(method = "adjustMovementForPiston", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Vec3d;x:D", opcode = Opcodes.GETFIELD))
    double adjustMovementForPiston$useX4(Vec3d instance){
        Vec4d movement = Vec4d.of(instance);
        return movement.x4;
    }

    @Redirect(method = "adjustMovementForPiston", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d adjustMovementForPiston$useVec4d(double x, double y, double z){
        return new Vec4d(x, y, z, 0);
    }

    @Inject(method = "adjustMovementForPiston", at = @At("TAIL"), cancellable = true)
    void adjustMovementForPiston$checkW(Vec3d movement3, CallbackInfoReturnable<Vec3d> cir){
        Vec4d movement = Vec4d.of(movement3);
        if (movement.w != 0.0) {
            double newW = this.calculatePistonMovementFactor(Direction4Constants.Axis4Constants.W, movement.w);
            cir.setReturnValue(Math.abs(newW) <= MathHelper.EPSILON ? Vec4d.ZERO : new Vec4d(0, 0, 0, newW));
        }
    }

    @WrapOperation(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShapes;calculateMaxOffset(Lnet/minecraft/util/math/Direction$Axis;Lnet/minecraft/util/math/Box;Ljava/lang/Iterable;D)D"
    ))
    private static double collisions$calculateMaxOffsetW(Direction.Axis axis, Box box, Iterable<VoxelShape> shapes, double maxDist, Operation<Double> original) {
        if (!axis.equals(Direction4Constants.Axis4Constants.W)) {
            return original.call(axis, box, shapes, maxDist);
        }
        Box4 box4 = Box4.converted(box);
        for (VoxelShape shape : shapes) {
            if (Math.abs(maxDist) < 1E-7) {
                return 0;
            }
            Box4 shapeBox = Box4.converted(shape.getBoundingBox());
            if (!box4.flatten().intersects(shapeBox.flatten()))
                continue; // Does not touch us in 3D, skipping
            if (maxDist > 0) {
                double distanceAnthOfBox  = shapeBox.minW - box4.maxW;
                if (distanceAnthOfBox  >= 0)
                    maxDist = Math.min(maxDist, distanceAnthOfBox );
            } else if (maxDist < 0) {
                double distanceKenthOfBox = shapeBox.maxW - box4.minW;
                if (distanceKenthOfBox <= 0)
                    maxDist = Math.max(maxDist, distanceKenthOfBox);
            }
        }
        return maxDist;
    }


    @Definition(id = "movement", local = @Local(type = Vec3d.class, argsOnly = true))
    @Definition(id = "x", field = "Lnet/minecraft/util/math/Vec3d;x:D")
    @Definition(id = "adjusted", local = @Local(type = Vec3d.class, ordinal = 1))
    @Expression("movement.x != adjusted.x")
    @ModifyExpressionValue(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    boolean collisions$checkIfAdjustedW(boolean original, @Local(argsOnly = true) Vec3d movement3, @Local(ordinal = 1) Vec3d adjusted3){
        Vec4d movement = Vec4d.of(movement3);
        Vec4d adjusted = Vec4d.of(adjusted3);
        return movement.x4 != adjusted.x4 || movement.w != adjusted.w;
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;", ordinal = 0
    ))
    Box collisions$stretch4(Box instance, double x, double y, double z, @Local(argsOnly = true) Vec3d movement3){
        Vec4d movement = Vec4d.of(movement3);
        return Box4.converted(instance).stretch(movement.x4, y, z, movement.w);
    }

    @WrapOperation(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;absMax(DD)D"), require = 1)
    double pushAwayFrom$absDW(
        double dx3, double dz, Operation<Double> original,
        @Local(argsOnly = true) Entity other,
        @Share("dx") LocalDoubleRef dxRef, @Share("dw") LocalDoubleRef dwRef
    ) {
        Vec4d thisPos = Vec4d.of(this.getEntityPos());
        Vec4d otherPos = Vec4d.of(other.getEntityPos());

        double dx = otherPos.x4 - thisPos.x4;
        dxRef.set(dx);
        double dw = otherPos.w - thisPos.w;
        dwRef.set(dw);

        return original.call(original.call(dx, dz), dw);
    }

    @Definition(id = "d", local = @Local(type = double.class, ordinal = 0))
    @Definition(id = "f", local = @Local(type = double.class, ordinal = 2))
    @Expression("d / @(f)")
    @ModifyExpressionValue(method = "pushAwayFrom", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0), require = 1)
    double pushAwayFrom$divideW(double divFactor, @Share("dx") LocalDoubleRef dxRef, @Share("dw") LocalDoubleRef dwRef){
        dxRef.set(dxRef.get() / divFactor);
        dwRef.set(dwRef.get() / divFactor);
        return divFactor;
    }

    @Definition(id = "d", local = @Local(type = double.class, ordinal = 0))
    @Definition(id = "g", local = @Local(type = double.class, ordinal = 3))
    @Expression("d * @(g)")
    @ModifyExpressionValue(method = "pushAwayFrom", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0), require = 1)
    double pushAwayFrom$multiplyW(double mulFactor, @Share("dx") LocalDoubleRef dxRef, @Share("dw") LocalDoubleRef dwRef){
        mulFactor *= 0.05f;
        dxRef.set(dxRef.get() * mulFactor);
        dwRef.set(dwRef.get() * mulFactor);
        return mulFactor;
    }

    @Definition(id = "addVelocity", method = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V")
    @Expression("this.addVelocity(?, ?, ?)")
    @Redirect(method = "pushAwayFrom", at = @At("MIXINEXTRAS:EXPRESSION"))
    void pushAwayFrom$thisAddVelocity(
        Entity This, double deltaX, double deltaY, double deltaZ,
        @Share("dx") LocalDoubleRef dxRef, @Share("dw") LocalDoubleRef dwRef
    ) {
        This.addVelocity(new Vec4d(-dxRef.get(), deltaY, deltaZ, -dwRef.get()));
    }

    @Definition(id = "addVelocity", method = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V")
    @Definition(id = "other", local = @Local(type = Entity.class, argsOnly = true))
    @Expression("other.addVelocity(?, ?, ?)")
    @Redirect(method = "pushAwayFrom", at = @At("MIXINEXTRAS:EXPRESSION"))
    void pushAwayFrom$otherAddVelocity(
        Entity other, double deltaX, double deltaY, double deltaZ,
        @Share("dx") LocalDoubleRef dxRef, @Share("dw") LocalDoubleRef dwRef
    ) {
        other.addVelocity(new Vec4d(dxRef.get(), deltaY, deltaZ, dwRef.get()));
    }
}
