package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
        extends WorldMixin
        implements StructureWorldAccess {

    @Shadow public abstract void updateNeighbors(BlockPos pos, Block block);

    @ModifyConstant(method = "setSpawnPos", constant = @Constant(intValue = 11))
    private int injectedStartRegionRange(int value) {
        return 5;
    }

    @Override
    public void updateNeighborsExcept4(BlockPos pos, Block sourceBlock, Direction4 direction) {
        this.updateNeighbors(pos, sourceBlock);
    }
}
