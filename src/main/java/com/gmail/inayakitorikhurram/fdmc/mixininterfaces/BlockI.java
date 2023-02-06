package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public interface BlockI {
    BlockState getDefaultState();
    void setDefaultBlockState(BlockState state);

    StateManager<Block, BlockState> getStateManager();
}
