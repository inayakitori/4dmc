package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class UnderSupport extends SupportStructure{

    Block supportBlock = Blocks.OAK_LEAVES;

    //places a leaf block underneath for support, dissapears once player moves out of space of that block
    protected UnderSupport(ServerPlayerEntity player) {
        super.world = player.getWorld();
        super.linkedPlayer = player;
        BlockPos playerBlockPos = player.getBlockPos();
        super.activeBox = new Box(playerBlockPos, playerBlockPos.add(0, 1, 0));
        super.pos = playerBlockPos.add(0, -1, 0);
    }

    @Override
    protected boolean placeSupport() {
        if(world.getBlockState(pos).isAir()){
            world.setBlockState(pos, supportBlock.getDefaultState());
            return true;
        }
        return false;
    }

    @Override
    protected boolean forceRemove() {
        if(world.getBlockState(pos).getBlock() == supportBlock){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return true;
        }
        return false;
    }
}
