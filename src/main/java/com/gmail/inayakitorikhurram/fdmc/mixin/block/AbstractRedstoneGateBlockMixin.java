package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.*;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;
import static net.minecraft.block.AbstractRedstoneGateBlock.POWERED;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends HorizontalFacingBlockMixin {
    
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


    //make **everything** use this property
    @Redirect(method = "*", at=@At(value = "FIELD", target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;FACING:Lnet/minecraft/state/property/DirectionProperty;"))
    private DirectionProperty fdmc$redirectFacingProperty(){
        return HORIZONTAL_FACING4;
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void getPlacementState4(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        OptionalDirection4 optionalDirection4 = ((CanStep)ctx.getPlayer()).getPlacementDirection4();
        //if should place ana/kata do that
        optionalDirection4.ifPresent(direction4 -> {
            BlockState state = cir.getReturnValue();
            cir.setReturnValue(state
                    .with(HORIZONTAL_FACING4, direction4.toDirection().getOpposite())//allows for horizontal interaction e.g furnace n stuff
            );
        });
    }

    @Inject(method = "getMaxInputLevelSides", at = @At("HEAD"), cancellable = true)
    protected void getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        Direction[] perpendicularDirections = ((Direction4)(Object)state.get(HORIZONTAL_FACING4)).getPerpendicularHorizontal();
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
