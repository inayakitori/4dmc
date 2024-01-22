package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PistonBlockEntity.class)
public class PistonBlockEntityMixin {
    @Inject(method = "pushEntities", at = @At("HEAD"), cancellable = true)
    private static void fdmc$pushEntities(World world, BlockPos pos, float progress, PistonBlockEntity blockEntity, CallbackInfo ci){
        Direction movementDirection = blockEntity.getMovementDirection();
        if(movementDirection.getAxis() != Direction4Constants.Axis4Constants.W) return;
        ci.cancel();
        //handle 4D entity movement

        //be at least half extended to push entity
        VoxelShape headBlockShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
        Box newHeadBox = headBlockShape
                .getBoundingBox()
                .offset(new BlockPos(movementDirection.getVector()));

        List<Entity> pushedEntities = world.getOtherEntities(null, newHeadBox);
        if (pushedEntities.isEmpty()) {
            return;
        }

        for(Entity entity : pushedEntities) {
            if (!(entity instanceof CanStep steppingEntity)) continue;
            steppingEntity.scheduleStep(movementDirection.getDirection().offset());
        }

    }
}
