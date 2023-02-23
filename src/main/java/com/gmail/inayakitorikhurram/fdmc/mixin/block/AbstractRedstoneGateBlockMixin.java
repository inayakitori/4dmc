package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.*;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends BlockMixin {
    @Shadow protected abstract int getInputLevel(WorldView world, BlockPos pos, Direction dir);

    @Redirect(method = "neighborUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] fdmc$expandDirections(){
        return Direction4Constants.VALUES;
    }

    @Inject(method = "getMaxInputLevelSides", at = @At("HEAD"), cancellable = true)
    protected void getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        Direction[] perpendicularDirections = ((Direction4)(Object)state.get(HorizontalFacingBlock.FACING)).getPerpendicularHorizontal();
        int max_val = 0;
        for (Direction dir4 : perpendicularDirections){
            max_val = Math.max(
                    max_val,
                    this.getInputLevel(world, pos.offset(dir4), dir4)
            );
        }
        cir.setReturnValue(max_val);
        cir.cancel();
    }
}
