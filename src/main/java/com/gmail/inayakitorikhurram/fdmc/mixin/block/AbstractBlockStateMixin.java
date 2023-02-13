package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
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
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
abstract class AbstractBlockMixin {

}
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

    //Right now this prevents models from trying to return whether it's kata/ana sides are solid and just assume they are if at least one of the Direction3 sides are solid
    @Inject(method = "isSideSolid", at = @At("HEAD"), cancellable = true)
    public void fdmc$isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
        if(direction.getAxis() == Direction4Constants.Axis4.W) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        //else continue finding dir3 solidness
    }


}
