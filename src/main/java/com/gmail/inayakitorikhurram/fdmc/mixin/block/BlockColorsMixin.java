package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColors;registerColorProvider(Lnet/minecraft/client/color/block/BlockColorProvider;[Lnet/minecraft/block/Block;)V", ordinal = 6))
    private static void modifyRedstone(BlockColors blockColors, BlockColorProvider provider, Block[] blocks){
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor(FDMCMath.getRedstoneColorIndex(state)), Blocks.REDSTONE_WIRE);
    }

}
