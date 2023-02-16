package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin
        extends State<Block, BlockState>
        implements AbstractBlockStateI {

    @Shadow protected abstract BlockState asBlockState();

    @Shadow public abstract Block getBlock();

    @Shadow public abstract boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction);

    @Shadow public abstract boolean isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType);

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

    private static final Direction[] KATA_ANA = new Direction[]{Direction4Constants.KATA, Direction4Constants.ANA};

    @Redirect(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;replaceWithStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;II)V"))
    private void updateNeightboursW(WorldAccess instance, Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        if (direction.getAxis() == Direction4Constants.Axis4Constants.W) {
            if (!((BlockSettings4Access) this.asBlockState().getBlock()).acceptsWNeighbourUpdates()) {
                return;
            }
        }
        instance.replaceWithStateForNeighborUpdate(direction.getOpposite(), this.asBlockState(), pos, neighborPos, flags, maxUpdateDepth);
    }

    //Right now this prevents models from trying to return whether it's kata/ana sides are solid and just assume they are if at least one of the Direction3 sides are solid
    @Inject(method = "isSideSolid", at = @At("HEAD"), cancellable = true)
    public void fdmc$isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
        if(direction.getAxis() == Direction4Constants.Axis4Constants.W) {
            AbstractBlock.AbstractBlockState abstractBlockState = (AbstractBlock.AbstractBlockState)(Object) this;
            cir.setReturnValue(abstractBlockState.isSideSolid(world, pos, Direction.NORTH, shapeType)
                            && abstractBlockState.isSideSolid(world, pos, Direction.SOUTH, shapeType)
                            && abstractBlockState.isSideSolid(world, pos, Direction.WEST, shapeType)
                            && abstractBlockState.isSideSolid(world, pos, Direction.EAST, shapeType));
            cir.cancel();
        }
        //else continue finding dir3 solidness
    }


}
