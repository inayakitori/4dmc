package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.WorldI;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Inject(method= "shouldExtend" ,at = @At("TAIL"), cancellable = true)
    private void shouldExtend(World world, BlockPos pos, Direction pistonFace, CallbackInfoReturnable<Boolean> cir) {
        for (Direction4 dir : Direction4.WDIRECTIONS) {
            if (/*dir != pistonFace && */((WorldI)world).isEmittingRedstonePower(pos.add(dir.getVec3()), dir)) {
                cir.setReturnValue(true);
                return;
            }
        }

        //quasi-connectivity, check powering of block above
        for (Direction4 dir : Direction4.WDIRECTIONS) {
            if (/*dir != pistonFace && */((WorldI)world).isEmittingRedstonePower(pos.up().add(dir.getVec3()), dir)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
