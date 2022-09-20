package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CryingObsidianBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class UnderSupport extends SupportStructure{

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");
    static BlockState supportBlock;

    static {
        supportBlock = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
    }


    //places a leaf block underneath for support, disappears once player moves out of space of that block
    protected UnderSupport(ServerPlayerEntity player, BlockPos finalPos, BlockPos prevPos) {
        super.supportTypeId = 1;

        super.world = player.getWorld();
        super.linkedPlayer = player;
        super.activeBox = new Box(finalPos, finalPos.add(1, 2, 1));
        super.finalPos = finalPos.add(0, -1, 0);
        super.prevPos = prevPos.add(0, -1, 0);
        super.stepDirection = ((CanStep) player).getStepDirection();
    }

    @Override
    protected boolean placeSupport() {
        //if there was already a supporting block and the new slice doesn't have a supporting block
        if(world.getBlockState(finalPos).isAir() && !world.getBlockState(prevPos).isAir() && !world.getBlockState(prevPos).isOf(supportBlock.getBlock())){
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
            LOGGER.info("Supports: removed Support");
        } else {
            LOGGER.info("Supports: support already removed");
        }
        return true;
    }
}
