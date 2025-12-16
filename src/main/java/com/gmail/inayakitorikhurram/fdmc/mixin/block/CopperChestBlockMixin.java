package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;

@Mixin(CopperChestBlock.class)
public abstract class CopperChestBlockMixin extends ChestBlock implements ChestBlockI{
    @Shadow
    public abstract boolean canMergeWith(BlockState state);

    public CopperChestBlockMixin(Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier, SoundEvent openSound, SoundEvent closeSound, Settings settings) {
        super(blockEntityTypeSupplier, openSound, closeSound, settings);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    private void neighbourUpdateAxis2(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir){
        //working off current state but also making sure the type2 connection isn't removed early
        state = cir.getReturnValue();

        if (this.canMergeWith(neighborState) && direction.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE_2);
            if (
                    chestType != ChestType.SINGLE &&
                    state.get(FACING) == neighborState.get(FACING) &&
                    ChestBlockI.getConnectionDirection(neighborState, ChestAdjacencyAxis.KATAANA).orElse(null) == direction.getOpposite()
            ) {
                cir.setReturnValue(
                        neighborState.getBlock().getStateWithProperties(state)
                );
            }
        }
    }
}
