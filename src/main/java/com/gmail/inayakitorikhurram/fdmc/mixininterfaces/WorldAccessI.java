package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.NeighbourUpdaterI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;

public interface WorldAccessI
        extends RegistryWorldView,
        LunarWorldView {

    default void replaceWithStateForNeighborUpdate(Direction4 direction4, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        NeighbourUpdaterI.replaceWithStateForNeighborUpdate((WorldAccess) this, direction4, neighborState, pos, neighborPos, flags, maxUpdateDepth - 1);
    }

    NeighborUpdater getNeighbourUpdater();

    void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction4 direction);
}
