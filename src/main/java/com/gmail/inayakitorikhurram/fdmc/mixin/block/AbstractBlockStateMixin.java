package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.WorldAccessI;
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
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
abstract class AbstractBlockMixin implements AbstractBlockI {

    @Shadow protected abstract Block asBlock();

    @Shadow @Deprecated public abstract BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }


    /**if we want to get the weak redstone output to the block at pos
     *
     * @param state the block that could be powered by the current block
     * @param world the world of powering
     * @param pos the position of the potentially powered block
     * @param dir the direction from the perspective of the block to be powered
     * @return the power level (it's under 9000)
     */
    @Override
    public int getWeakRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir) {
        return 0;
    }

    /**if we want to get the strong redstone output to the block at pos
     *
     * @param state the block that could be powered by the current block
     * @param world the world of powering
     * @param pos the position of the potentially powered block
     * @param dir the direction from the perspective of the block to be powered
     * @return the power level (it's under 9000)
     */
    @Override
    public int getStrongRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir) {
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

    @Override
    public BlockState getStateForNeighborUpdate(Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return ((AbstractBlockI)getBlock()).getStateForNeighborUpdate(this.asBlockState(), direction, neighborState, world, pos, neighborPos);
    }
    @Override
    public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction4 dir) {
        return ((AbstractBlockI)getBlock()).getWeakRedstonePower4(this.asBlockState(), world, pos, dir);
    }

    @Override
    public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction4 dir) {
        return ((AbstractBlockI)getBlock()).getStrongRedstonePower4(this.asBlockState(), world, pos, dir);
    }

    @Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", at = @At("TAIL") )
    public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
        BlockPos.Mutable neighbourPos = new BlockPos.Mutable();
        for (Direction4 dir : Direction4.WDIRECTIONS) {
            neighbourPos.set(pos.add(dir.getVec3()));
            ((WorldAccessI)world).replaceWithStateForNeighborUpdate(dir,
                    asBlockState(), neighbourPos, pos, flags, maxUpdateDepth);
        }
    }

}
