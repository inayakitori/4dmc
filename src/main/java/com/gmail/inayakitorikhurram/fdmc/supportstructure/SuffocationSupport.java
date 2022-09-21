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

    protected SuffocationSupport(ServerPlayerEntity player, BlockPos finalPos, BlockPos prevPos) {
        super.supportTypeId = 2;
        super.MIN_LIFETIME = 0;

        super.stepDirection = ((CanStep) player).getStepDirection();

        super.world = player.getWorld();
        super.linkedPlayer = player;
        super.activeBox = linkedPlayer.getBoundingBox().offset(FDMCConstants.STEP_DISTANCE * stepDirection, 0.01, 0);
        super.finalPos = finalPos;
        super.prevPos = prevPos;
    }

    @Override
    protected boolean placeSupport() {
        return ((CanStep) linkedPlayer).isStepping();
    }



    //if:
    //1) the player has moved or
    //2) there isn't anything in the stepped into area
    // and the player isn't teleporting
    // the support should be removed.
    //the support should always be removed if the player has disconnected or the support has expired
    @Override
    protected boolean shouldRemove() {
        return (
                lifetime > MIN_LIFETIME
                        && !linkedPlayer.isInTeleportationState()
                        && (
                                !activeBox.intersects(linkedPlayer.getBoundingBox())
                                || !hasIntersection()
                            )

        )
                || linkedPlayer.isDisconnected()
                || lifetime > MAX_LIFETIME;
    }

    @Override
    protected boolean forceRemove() {
        ((CanStep)linkedPlayer).setStepping(false);
        ServerPlayNetworking.send(linkedPlayer, FDMCConstants.MOVED_PLAYER_ID, PacketByteBufs.empty());
        return true;
    }

    protected boolean hasIntersection(){
        return BlockPos.stream(activeBox).anyMatch(pos -> {
            BlockState blockState = this.world.getBlockState((BlockPos)pos);
            return !blockState.isAir() &&
                    blockState.shouldSuffocate(this.world, (BlockPos)pos) &&
                    VoxelShapes.matchesAnywhere(
                            blockState.getCollisionShape(
                                    this.world,
                                    (BlockPos)pos).offset(pos.getX(), pos.getY(), pos.getZ()),
                            VoxelShapes.cuboid(activeBox),
                            BooleanBiFunction.AND);
        });
    }

}
