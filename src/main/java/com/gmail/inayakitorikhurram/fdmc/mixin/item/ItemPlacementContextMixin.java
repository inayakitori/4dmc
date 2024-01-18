package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemPlacementContext.class)
public abstract class ItemPlacementContextMixin {
    @Inject(method = "offset", at = @At("HEAD"), cancellable = true)
    private static void offsetCheckIf4D(ItemPlacementContext context, BlockPos pos, Direction side, CallbackInfoReturnable<ItemPlacementContext> cir) {
        if (context instanceof ItemPlacementContext4) {
            cir.setReturnValue(new ItemPlacementContext4(
                    context.getWorld(),
                    context.getPlayer(),
                    context.getHand(),
                    context.getStack(),
                    new BlockHitResult(
                            new Vec3d((double)pos.getX() + 0.5 + (double)side.getOffsetX() * 0.5,
                                    (double)pos.getY() + 0.5 + (double)side.getOffsetY() * 0.5,
                                    (double)pos.getZ() + 0.5 + (double)side.getOffsetZ() * 0.5),
                            side,
                            pos,
                            false
                    )
            ));
            cir.cancel();
        }
    }
}
