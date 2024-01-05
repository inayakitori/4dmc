package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;


public class UnderSupport extends SupportStructure{

    static BlockState supportBlock;

    static {
        supportBlock = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
    }


    //TODO factory for this instead
    //places a leaf block underneath for support, disappears once player moves out of space of that block
    protected UnderSupport(Entity linkedEntity, BlockPos finalPos, BlockPos prevPos) {
        super.supportTypeId = 1;

        super.world = (ServerWorld) linkedEntity.getWorld();
        super.linkedEntity = linkedEntity;
        if(linkedEntity instanceof ServerPlayerEntity){
            super.linkedPlayer = (ServerPlayerEntity) linkedEntity;
            hasLinkedPlayer = true;
        }
        super.activeBox = new Box(finalPos.toCenterPos(), finalPos.add(1, 2, 1).toCenterPos());
        super.finalPos = finalPos.add(0, -1, 0);
        super.prevPos = prevPos.add(0, -1, 0);
        super.stepDirection = ((CanStep) linkedEntity).getStepDirection();
    }

    @Override
    protected boolean placeSupport() {
        //if there was already a supporting block and the new slice doesn't have a supporting block
        if(
                world.getBlockState(finalPos).isAir() &&
                !world.getBlockState(prevPos).isAir() &&
                !world.getBlockState(prevPos).isOf(supportBlock.getBlock()) &&
                !((CanStep)linkedEntity).doesCollideWithBlocksAt(prevPos.add(0,1,0))
        ){
            world.setBlockState(finalPos, supportBlock);
            //LOGGER.info("Supports: placed Support");
            return true;
        }
        return false;
    }

    @Override
    protected boolean forceRemove() {
        if(world.getBlockState(finalPos).getBlock() == supportBlock.getBlock()){
            world.setBlockState(finalPos, Blocks.AIR.getDefaultState());
            //FDMCConstants.LOGGER.info("Supports: removed Support");
        } else {
            //FDMCConstants.LOGGER.info("Supports: support already removed");
        }
        return true;
    }
}
