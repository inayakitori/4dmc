package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Shadow
    @Nullable
    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        return null;
    }

    @Inject(method="getOutputInventory", at = @At("HEAD"), cancellable = true)
    private static void getOutputInventory4(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Inventory> cir){
        Direction4 dir4 = state.get(FDMCProperties.FACING4).toDir4(state.get(HopperBlock.FACING));
        BlockPos outputPos = pos.add(dir4.getVec3());
        Inventory outputInventory = getInventoryAt(world, outputPos);
        if(outputInventory == null) {
            //ok this is so jank but basically if we return a null inventory the fabric api tries to find one itself
            // which we don't want. So instead we just return the hopper itself so it's non-null check return true
            outputInventory = (Inventory) world.getBlockEntity(pos);
        }
        cir.setReturnValue(outputInventory);
        cir.cancel();
    }

    @Inject(method = "insert", at = @At("TAIL"))
    private static void insert4(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        FDMCConstants.LOGGER.info("output inventory: {} {}", getOutputInventory(world, pos, state), cir.getReturnValue());
    }

    @Shadow
    public static Inventory getInventoryAt(World world, BlockPos offset) {
        return null;
    }
}
