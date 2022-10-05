package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.NeighbourUpdaterI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.WorldAccessI;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldAccessI{

    @ModifyConstant(method = "setSpawnPos", constant = @Constant(intValue = 11))
    private int injectedStartRegionRange(int value) {
        return 5;
    }

    @Override
    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction4 direction) {
        ((NeighbourUpdaterI)getNeighbourUpdater()).updateNeighbors(pos, sourceBlock, direction);
    }

}
