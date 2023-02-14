package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.*;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4Owner;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends HorizontalFacingBlockMixin implements Property4Owner {
    
    @Shadow protected abstract int getOutputLevel(BlockView world, BlockPos pos, BlockState state);
    @Shadow
    public static boolean isRedstoneGate(BlockState state) {
        return false;
    }
    @Shadow protected abstract void updateTarget(World world, BlockPos pos, BlockState state);
    @Shadow protected abstract boolean isValidInput(BlockState state);


    @Shadow protected abstract int getPower(World world, BlockPos pos, BlockState state);


    @Shadow protected abstract int getInputLevel(WorldView world, BlockPos pos, Direction dir);

    @Redirect(method = "neighborUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] fdmc$expandDirections(){
        return Direction4Constants.VALUES;
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getPlayerFacing()Lnet/minecraft/util/math/Direction;"))
    public Direction getPlacementState4(ItemPlacementContext instance){
        Optional<Direction> optionalDirection4 = Optional.ofNullable((CanStep)instance.getPlayer())
                .flatMap(CanStep::getPlacementDirection4);

        AtomicReference<Direction> facingDir = new AtomicReference<>(instance.getPlayerFacing());
        optionalDirection4.ifPresent(direction4 -> {
            facingDir.set(direction4);
        });
        return facingDir.get();
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
