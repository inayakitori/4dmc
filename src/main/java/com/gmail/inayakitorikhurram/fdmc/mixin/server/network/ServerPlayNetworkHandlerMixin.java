package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Entity4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Pos4Extension;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

	@Shadow
	private static double clampHorizontal(double d) { return 0; }

	@Shadow
	private static double clampVertical(double d) { return 0; }

	@Shadow
    private double lastTickY;
    @Shadow
    private double lastTickZ;
    @Unique
    private double lastTickX4, lastTickW, updatedX4, updatedW;

    @ModifyExpressionValue(
        method = "onPlayerMove",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isMovementInvalid(DDDFF)Z")
    )
    boolean isMovementInvalid4(boolean original, @Local(argsOnly = true) PlayerMoveC2SPacket packet) {
        Pos4Extension pos4 = (Pos4Extension) packet;
        return original
            || Double.isNaN(pos4.getX4(0.0))
            || Double.isNaN(pos4.getW(0.0));
    }

    @Inject(method = "syncWithPlayerPosition", at = @At("TAIL"))
    void syncWithPlayerPosition4(CallbackInfo ci) {
        Vec4d playerPos = Vec4d.of(this.player.getEntityPos());
        this.updatedX4 = this.lastTickX4 = playerPos.x4;
        this.updatedW = this.lastTickW = playerPos.w;
    }

    @Expression(value = "?*? + ?*? + ?*?")
    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    double onPlayerMove$calcMoveDistance4(
        double original, @Local(argsOnly = true) PlayerMoveC2SPacket packet3,
        @Share("clamp") LocalRef<Vec4d> clamp
    ) {
        Pos4Extension packet4 = (Pos4Extension) packet3;
        Vec4d playerPos = Vec4d.of(this.player.getEntityPos());
        clamp.set(new Vec4d(
            clampHorizontal(packet4.getX4(playerPos.x4)),
            clampVertical  (packet3.getY (playerPos.y )),
            clampHorizontal(packet3.getZ (playerPos.z )),
            clampHorizontal(packet4.getW (playerPos.w ))
        ));
        return clamp.get().subtract(
            this.lastTickX4,
            this.lastTickY,
            this.lastTickZ,
            this.lastTickW
        ).lengthSquared();
    }

    @Redirect(method = "onPlayerMove", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d onPlayerMove$move4(
        double x, double y, double z,
        @Share("clamp") LocalRef<Vec4d> clamp
    ){
        return new Vec4d(clamp.get().x4 - this.updatedX4, y, z, clamp.get().w - this.updatedW);
    }

    @Expression(value = "?*? + ?*? + ?*?")
    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    double onPlayerMove$calcMoveDistance4_2(double original, @Share("clamp") LocalRef<Vec4d> clamp) {
        Vec4d playerPos = Vec4d.of(this.player.getEntityPos());
        Vec4d distance = clamp.get().subtract(playerPos);

        if (distance.y > -0.5 || distance.y < 0.5) {
            distance = distance.withAxis(Direction.Axis.Y, 0);
        }
        return distance.lengthSquared();
    }

    @Redirect(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V", ordinal = 0))
    void onPlayerMove$followVehicle4(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch){
        Vec4d playerPos = Vec4d.of(player.getEntityPos());
        ((Entity4) player).updatePositionAndAngles(playerPos, yaw, pitch);
    }

    @Redirect(method = "onPlayerMove", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isEntityNotCollidingWithBlocks(Lnet/minecraft/world/WorldView;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;DDD)Z"
    ))
    boolean checkCollisions4D(ServerPlayNetworkHandler instance, WorldView world, Entity entity, Box boundingBox, double newX, double newY, double newZ, @Share("clamp") LocalRef<Vec4d> clamp) {
        Box boundingBotAtFuturePos = entity.getBoundingBox().offset(clamp.get().subtract(entity.getEntityPos()));
        Iterable<VoxelShape> iterable = world.getCollisions(entity, boundingBotAtFuturePos.contract(MathHelper.EPSILON), boundingBox.getHorizontalCenter());
        VoxelShape voxelShape = VoxelShapes.cuboid(boundingBox.contract(MathHelper.EPSILON));
        for (VoxelShape voxelShape2 : iterable) {
            if (VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) continue;
            return true;
        }
        return false;
    }


    @Redirect(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V", ordinal = 1))
    void onPlayerMove$setNewPositionServerside(
        ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch,
        @Share("clamp") LocalRef<Vec4d> clamp
    ){
        ((Entity4) player).updatePositionAndAngles(clamp.get(), yaw, pitch);
    }

    @Inject(method = "onPlayerMove", at = @At(value = "TAIL"))
    void onPlayerMove$setUpdated4(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        Vec4d playerPos = Vec4d.of(this.player.getEntityPos());
        this.updatedX4 = playerPos.x4;
        this.updatedW = playerPos.w;
    }
}
