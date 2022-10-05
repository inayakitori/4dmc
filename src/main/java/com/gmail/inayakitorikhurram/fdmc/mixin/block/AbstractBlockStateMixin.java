package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
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
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
abstract class AbstractBlockMixin implements AbstractBlockI {

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
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

    @Override
    public BlockState getStateForNeighborUpdate(Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return ((AbstractBlockI)getBlock()).getStateForNeighborUpdate(this.asBlockState(), direction, neighborState, world, pos, neighborPos);
    }

    @Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", at = @At("TAIL") )
    public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
        BlockPos.Mutable neighbourPos = new BlockPos.Mutable();
        for (int w = -1; w < 2 ; w += 2) {
            neighbourPos.set(pos.add(FDMCMath.getOffset(w)));
            world.replaceWithStateForNeighborUpdate(w < 0 ? Direction.EAST : Direction.WEST,
                    asBlockState(), neighbourPos, pos, flags, maxUpdateDepth);
        }
    }
}
