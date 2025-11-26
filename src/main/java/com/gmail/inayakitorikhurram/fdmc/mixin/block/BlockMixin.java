package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin implements ItemConvertible {
    @Shadow protected abstract Block asBlock();

    @Shadow
    public abstract BlockState getDefaultState();

    @Shadow
    protected abstract void setDefaultState(BlockState state);

    @Shadow
    public abstract BlockState getPlacementState(ItemPlacementContext ctx);

//    @Inject(method = "postProcessState", at = @At("RETURN"))
//    private static void fdmc$useAllDirections(BlockState state, WorldAccess world, BlockPos pos, CallbackInfoReturnable<BlockState> cir,
//                                              @Local(ordinal = 1) LocalRef<BlockState> blockState){
//
//        for(Direction direction : Direction4Constants.Type4.HORIZONTAL4) {
//            BlockPos mutable = pos.offset(direction);
//            if(world.isChunkLoaded(ChunkSectionPos.getSectionCoord(mutable.getX()), ChunkSectionPos.getSectionCoord(mutable.getY())) &&
//                    world.getChunk(mutable).getStatus().isAtLeast(ChunkStatus.FULL)){
////                blockState.set(
////                        blockState.get().getStateForNeighborUpdate(world, world, pos, direction, mutable, world.getBlockState(mutable), world.getRandom())
////                );
//            }
//        }
//    }

}
