package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters.NeighbourUpdaterMixinI;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;

public interface WorldAccessMixinI
        extends RegistryWorldView,
        LunarWorldView {

    default void replaceWithStateForNeighborUpdate(Direction4 direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        NeighbourUpdaterMixinI.replaceWithStateForNeighborUpdate((WorldAccess) this, direction4, neighborState, pos, neighborPos, flags, maxUpdateDepth - 1);
    }
}
