package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(WallMountedBlock.class)
public class WallMountedBlockMixin {

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getPlayerFacing()Lnet/minecraft/util/math/Direction;"))
    public Direction getPlacementState4(ItemPlacementContext ctx){
        return MixinUtil.modifyPlacementDirection(ctx, ctx::getPlayerFacing);
    }
}
