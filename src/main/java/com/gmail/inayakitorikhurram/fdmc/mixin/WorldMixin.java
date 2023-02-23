package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.World.class)
public abstract class WorldMixin implements WorldAccess {
    //use HORIZONTAL4
    @Redirect(
            method = {
                    "updateComparators"
            },
            at=@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"
            )
    )
    private Direction.Type fdmc$redirectToHorizontal4(){
        return Direction4Constants.Type4.HORIZONTAL4;
    }

    @Inject(method = "isReceivingRedstonePower", at = @At(value = "TAIL"), cancellable = true)
    public void isReceivingRedstonePower(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
            cir.getReturnValueZ() ||
                    this.getEmittedRedstonePower(pos.offset(Direction4Constants.KATA), Direction4Constants.KATA) > 0 ||
                    this.getEmittedRedstonePower(pos.offset(Direction4Constants.ANA), Direction4Constants.ANA) > 0
        );
    }

    @Inject(method = "getReceivedStrongRedstonePower", at = @At("TAIL"), cancellable = true)
    public void getReceivedStrongRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {

        int i = cir.getReturnValueI();
        if(i >= 15){
            return;
        }

        i = Math.max(i, this.getStrongRedstonePower(pos.offset(Direction4Constants.KATA), Direction4Constants.KATA));
        if(i >= 15){
            cir.setReturnValue(i);
            return;
        }

        i = Math.max(i, this.getStrongRedstonePower(pos.offset(Direction4Constants.ANA), Direction4Constants.ANA));
        cir.setReturnValue(i);
    }

    @Redirect(method = "getReceivedRedstonePower", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;DIRECTIONS:[Lnet/minecraft/util/math/Direction;"))
    private Direction[] fdmc$getReceivedRedstonePowerUseAllDirections(){
        return Direction4Constants.VALUES;
    }

    @Shadow public abstract int getEmittedRedstonePower(BlockPos pos, Direction direction);
}
