package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBlock.class)
abstract class AbstractBlockMixin implements AbstractBlockI {

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction4 dir) {
        return 0;
    }
}
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin
        extends State<Block, BlockState>
        implements AbstractBlockStateI {

    @Shadow protected abstract BlockState asBlockState();

    @Shadow public abstract Block getBlock();

    protected AbstractBlockStateMixin(Block owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<BlockState> codec) {
        super(owner, entries, codec);
    }

    @Redirect(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", at = @At(value = "FIELD", target = "Lnet/minecraft/block/AbstractBlock;DIRECTIONS:[Lnet/minecraft/util/math/Direction;"))
    private Direction[] fdmc$modifyUpdateDirections(){

        return new Direction[]{
                Direction4Constants.WEST,
                Direction4Constants.EAST,
                Direction4Constants.NORTH,
                Direction4Constants.SOUTH,
                Direction4Constants.KATA,
                Direction4Constants.ANA,
                Direction4Constants.DOWN,
                Direction4Constants.UP
        };
    }


}
