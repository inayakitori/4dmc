package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;


public class SuffocationSupport extends SupportStructure{

    protected SuffocationSupport(Entity linkedEntity, BlockPos finalPos, BlockPos prevPos) {
        super.supportTypeId = 2;
        super.MIN_LIFETIME = -1;

        super.stepDirection = ((CanStep) linkedEntity).getStepDirection();

        super.world = (ServerWorld) linkedEntity.getWorld();
        super.linkedEntity = linkedEntity;
        if(linkedEntity instanceof ServerPlayerEntity){
            super.linkedPlayer = (ServerPlayerEntity) linkedEntity;
            super.hasLinkedPlayer = true;
        }
        super.activeBox = new Box(finalPos, finalPos.add(1, 2, 1));
        super.finalPos = finalPos;
        super.prevPos = prevPos;
    }

    @Override
    protected boolean placeSupport() {
        return ((CanStep) linkedEntity).isStepping();
    }



    //if:
    //1) the player has moved or
    //2) there isn't anything in the stepped into area
    // and the player isn't teleporting
    // the support should be removed.
    //the support should always be removed if the player has disconnected or the support has expired
    @Override
    protected boolean shouldRemove() {
        if(hasLinkedPlayer) {
            return (
                    lifetime > MIN_LIFETIME
                            && !linkedPlayer.isInTeleportationState()
                            && (
                            !activeBox.intersects(getExpandedBoundingBox())
                                    || !hasIntersection()
                    )

            )
                    || linkedPlayer.isDisconnected()
                    || lifetime > MAX_LIFETIME;
        } else{
            return (
                    lifetime > MIN_LIFETIME
                            && (
                            !activeBox.intersects(getExpandedBoundingBox())
                                    || !hasIntersection()
                    )

            )
                    || lifetime > MAX_LIFETIME;
        }
    }

    @Override
    protected boolean forceRemove() {
        if(hasLinkedPlayer) {
            ((CanStep) linkedEntity).setSteppingGlobally(linkedPlayer, 0, null);
        } else{
            //this doesn't happen yet, entities can't step
        }
        return true;
    }

    @Override
    protected Box getExpandedBoundingBox() {
        return new Box(linkedEntity.getPos(), linkedEntity.getPos().withBias(Direction.UP, 2));
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
