package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin extends State<Block, BlockState> {

    @Shadow
    protected abstract BlockState asBlockState();

    protected AbstractBlockStateMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
        super(owner, propertyMap, codec);
    }

    @Redirect(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/AbstractBlock;DIRECTIONS:[Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC))
    private Direction[] fdmc$modifyUpdateDirections(){
        return Direction4Constants.BLOCK_UPDATE_ORDER;
    }

    @Redirect(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;replaceWithStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)V"))
    private void updateNeightboursW(WorldAccess instance, Direction direction, BlockPos neighborPos, BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (direction.getAxis() == Direction4Constants.Axis4Constants.W) {
            if (!((BlockSettings4Access) instance.getBlockState(neighborPos).getBlock()).acceptsWNeighbourUpdates()) {
                return;
            }
        }
        instance.replaceWithStateForNeighborUpdate(direction, neighborPos, pos, state, flags, maxUpdateDepth);
    }

    //Right now this prevents models from trying to return whether it's kata/ana sides are solid and just assume they are if all horizontal Direction3 sides are solid
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
