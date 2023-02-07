package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlockMixin
        implements ItemConvertible, BlockI {
    @Shadow protected abstract void setDefaultState(BlockState state);

    @Shadow
    public abstract BlockState getDefaultState();

    @Override
    public void setDefaultBlockState(BlockState state) {
        setDefaultState(state);
    }

    @Shadow public abstract StateManager<Block, BlockState> getStateManager();
}
