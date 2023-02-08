package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.FACING4;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends AbstractRedstoneGateBlock {

    @Shadow @Final public static EnumProperty<ComparatorMode> MODE;

    protected ComparatorBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow protected abstract int getPower(World world, BlockPos pos, BlockState state);

    @Redirect(method="<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComparatorBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private void insertDirection4Property(ComparatorBlock instance, BlockState blockState){
        ((BlockI)instance).setDefaultBlockState(
                ((BlockI)instance).getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(FACING4, OptionalDirection4.NONE)
                        .with(POWERED, false)
                        .with(MODE, ComparatorMode.COMPARE)
        );
    }


    //this 4-property doesn't actually do anything unless it's set to kata or ana, the rest are a "None" type which just allows the block to take control
    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties4D(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FDMCProperties.FACING4);
    }


    //complete rewrite, could be an injection
    @Inject(method = "getPower", at = @At("HEAD"), cancellable = true)
    protected void getPower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        int i = super.getPower(world, pos, state);
        Direction4 dir4 = state.get(FACING4).toDir4(state.get(FACING));
        BlockPos blockPos = pos.add(dir4.getVec3());
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasComparatorOutput()) {
            i = blockState.getComparatorOutput(world, blockPos);
        } else if (i < 15 && blockState.isSolidBlock(world, blockPos)) {
            blockPos = blockPos.add(dir4.getVec3());
            blockState = world.getBlockState(blockPos);
            ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame4(world, dir4, blockPos);
            int j = Math.max(itemFrameEntity == null ? Integer.MIN_VALUE : itemFrameEntity.getComparatorPower(), blockState.hasComparatorOutput() ? blockState.getComparatorOutput(world, blockPos) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }
        cir.setReturnValue(i);
        cir.cancel();
    }

    private ItemFrameEntity getAttachedItemFrame4(World world, Direction4 dir4, BlockPos pos) {
        Optional<Direction> dir3 = dir4.getDirection3();
        List<ItemFrameEntity> list = world.getEntitiesByClass(ItemFrameEntity.class, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), itemFrame -> itemFrame != null && (dir3.isEmpty() || itemFrame.getHorizontalFacing() == dir3.get()));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

}
