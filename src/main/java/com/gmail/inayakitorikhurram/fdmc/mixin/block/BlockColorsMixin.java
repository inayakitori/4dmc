package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    @Shadow public abstract void registerColorProperty(Property<?> property, Block... blocks);

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColors;registerColorProvider(Lnet/minecraft/client/color/block/BlockColorProvider;[Lnet/minecraft/block/Block;)V", ordinal = 6))
    private static void modifyRedstone(BlockColors blockColors, BlockColorProvider provider, Block[] blocks){
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor(FDMCMath.getRedstoneColorIndex(state)), Blocks.REDSTONE_WIRE);
    }

    /*
    @Inject(method = "create", at = @At(value = "RETURN"), cancellable = true)
    private static void modifyBlockColors(CallbackInfoReturnable<BlockColors> cir) {
        BlockColors blockColors = cir.getReturnValue();

        blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
            return 8431445;
        }, Blocks.HOPPER);

        cir.setReturnValue(blockColors);
    } */

}
