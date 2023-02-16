package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.ObserverBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin {
    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getPlayerLookDirection()Lnet/minecraft/util/math/Direction;"))
    public Direction getPlacementState4(ItemPlacementContext ctx){
        return MixinUtil.modifyPlacementDirection(ctx, ctx::getPlayerLookDirection);
    }
}
