package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {

    @Inject(method = "pushEntities", at = @At("HEAD"), cancellable = true)
    private static void fdmc$pushEntities(World world, BlockPos pos, float progress, PistonBlockEntity blockEntity, CallbackInfo ci){
        Direction movementDirection = blockEntity.getMovementDirection();
        if(movementDirection.getAxis() != Direction4Constants.Axis4Constants.W){

            //FDMCConstants.LOGGER.info("3D Pushing entity {} {} {} in {}", blockEntity, pos, movementDirection, progress);
            return;
        }
        ci.cancel();
        //handle 4D entity movement

        //be at least half extended to push entity
        VoxelShape headBlockShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
        if(headBlockShape.isEmpty()) return;
        Box newHeadBox = headBlockShape
                .getBoundingBox()
                .offset(pos);

        List<Entity> pushedEntities = world.getOtherEntities(null, newHeadBox);
        if (pushedEntities.isEmpty()) {
            return;
        }

        for(Entity entity : pushedEntities) {
            PistonBlockEntity.moveEntity(movementDirection, entity, Float.NaN, movementDirection);
            //FDMCConstants.LOGGER.info("4D Pushed entity {} {} {} in {} {}", entity, blockEntity, pos, movementDirection, progress);
        }

    }

    @Redirect(method = "moveEntity", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private static Vec3d fdmc$fixPistonMovement(
        double x, double y, double z,
        @Local(argsOnly = true) double distance,
        @Local(argsOnly = true, ordinal = 1) Direction movementDirection
    ){
        return Vec4d.of(Direction4.asDirection4(movementDirection).getVector4()).multiply(distance);
    }

}
