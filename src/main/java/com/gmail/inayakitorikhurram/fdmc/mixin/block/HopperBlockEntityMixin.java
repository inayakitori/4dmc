package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Inject(method="getOutputInventory", at = @At("HEAD"), cancellable = true)
    private static void getOutputInventory4(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Inventory> cir){
        OptionalDirection4 optionalDirection4 = state.get(FDMCProperties.FACING4);
        optionalDirection4.ifPresent(direction4 -> {
                    cir.setReturnValue(
                            getInventoryAt(world, pos.add(direction4.getVec3()))
                    );
                    cir.cancel();
                }
            );
    }

    @Shadow
    public static Inventory getInventoryAt(World world, BlockPos offset) {
        return null;
    }
}
