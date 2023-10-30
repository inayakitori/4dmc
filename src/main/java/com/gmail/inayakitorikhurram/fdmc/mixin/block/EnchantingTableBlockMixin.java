package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin {
    @Shadow @Final @Mutable
    public static List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(BlockPos4.newBlockPos4(-2, 0, -2, -2).asBlockPos(), BlockPos4.newBlockPos4(2, 1, 2, 2).asBlockPos()).filter(pos -> {
        BlockPos4<?, ?> pos4 = BlockPos4.asBlockPos4(pos);
        return Math.abs(pos4.getX4()) == 2 || Math.abs(pos4.getZ4()) == 2 || Math.abs(pos4.getW4()) == 2;
    }).map(BlockPos::toImmutable).toList();

    // technically the original method would also kinda work here. Wouldn't work after x/w split though.
    @Inject(method = "canAccessPowerProvider", at = @At("HEAD"), cancellable = true)
    private static void canAccessPowerProvider(World world, BlockPos tablePos, BlockPos bookshelfOffset, CallbackInfoReturnable<Boolean> cir) {
        BlockPos4<?, ?> offset4 = BlockPos4.asBlockPos4(bookshelfOffset);
        BlockPos4<?, ?> tablePos4 = BlockPos4.asBlockPos4(tablePos); //tablePos.add(bookshelfOffset.getX() / 2, bookshelfOffset.getY(), bookshelfOffset.getZ() / 2)
        cir.setReturnValue(world.getBlockState(tablePos.add(bookshelfOffset)).isOf(Blocks.BOOKSHELF) && world.isAir(tablePos4.add4(offset4.getX4() / 2, offset4.getY4(), offset4.getZ4() / 2, offset4.getW4() / 2).asBlockPos()));
    }
}
