package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("rawtypes")
@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Redirect(method = "isWaterNearby", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;iterate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;"))
    private static Iterable<BlockPos> modifiedWaterSearch(BlockPos start, BlockPos end){

        BlockPos start4 = (BlockPos) ((BlockPos4)start).add4(0, 0, 0, -4).asVec3i();

        BlockPos end4   = (BlockPos) ((BlockPos4)end).add4(0, 0, 0, 4).asVec3i();

        return BlockPos.iterate(start4, end4);
    }
}
