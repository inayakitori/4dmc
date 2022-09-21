package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;

public class SuffocationSupport extends SupportStructure{

    protected int MIN_LIFETIME = 0;

    protected SuffocationSupport(ServerPlayerEntity player, BlockPos finalPos, BlockPos prevPos) {
        super.supportTypeId = 2;
        super.stepDirection = ((CanStep) player).getStepDirection();

        super.world = player.getWorld();
        super.linkedPlayer = player;
    }

    @Override
    protected boolean placeSupport() {
        return ((CanStep) linkedPlayer).isStepping() && hasIntersection();
    }

    @Override
    protected boolean shouldRemove() {
        return (lifetime > MIN_LIFETIME &&
                !hasIntersection() ) ||
                lifetime > MAX_LIFETIME;
    }

    @Override
    protected boolean forceRemove() {
        ((CanStep)linkedPlayer).setStepping(false);
        ServerPlayNetworking.send(linkedPlayer, FDMCConstants.MOVED_PLAYER_ID, PacketByteBufs.empty());
        return true;
    }

    protected boolean hasIntersection(){
        Box box = linkedPlayer.getBoundingBox().offset(0, 0.01, 0);
        return BlockPos.stream(box).anyMatch(pos -> {
            BlockState blockState = this.world.getBlockState((BlockPos)pos);
            return !blockState.isAir() &&
                    blockState.shouldSuffocate(this.world, (BlockPos)pos) &&
                    VoxelShapes.matchesAnywhere(
                            blockState.getCollisionShape(
                                    this.world,
                                    (BlockPos)pos).offset(pos.getX(), pos.getY(), pos.getZ()),
                            VoxelShapes.cuboid(box),
                            BooleanBiFunction.AND);
        });
    }

}
