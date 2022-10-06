package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlockMixin
        implements ItemConvertible {
    @Shadow public abstract BlockState getDefaultState();
}
