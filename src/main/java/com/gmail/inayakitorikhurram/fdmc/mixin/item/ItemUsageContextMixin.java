package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {

    @Shadow @Final private @Nullable PlayerEntity player;


    //if the player is trying to place/face w then let them.
    @Inject(method = "getHorizontalPlayerFacing", at = @At("RETURN"), cancellable = true)
    public void getHorizontalPlayerFacing(CallbackInfoReturnable<Direction> cir) {
        if (!((ItemUsageContext)(Object)this instanceof ItemPlacementContext4)) return;
        CanStep.of(this.player).flatMap(CanStep::getPlacementDirection4).ifPresent((direction -> {
            cir.setReturnValue(direction);
            cir.cancel();
        }));
    }

}
