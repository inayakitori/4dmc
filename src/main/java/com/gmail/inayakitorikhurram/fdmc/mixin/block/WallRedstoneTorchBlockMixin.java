package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.state.property.DirectionProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;

@Mixin(WallRedstoneTorchBlock.class)
public class WallRedstoneTorchBlockMixin {
    //make **everything** use this repeater property instead
}
