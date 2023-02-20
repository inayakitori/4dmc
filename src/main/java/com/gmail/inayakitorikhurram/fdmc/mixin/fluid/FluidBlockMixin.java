package com.gmail.inayakitorikhurram.fdmc.mixin.fluid;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Shadow @Final @Mutable
    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
            Direction.DOWN,
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST,
            Direction4Constants.KATA,
            Direction4Constants.ANA
    );
}
