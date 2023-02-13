package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import net.minecraft.block.ComparatorBlock;
import net.minecraft.state.property.DirectionProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;


@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends AbstractRedstoneGateBlockMixin {
    @Redirect(method = "*", at=@At(value = "FIELD", target = "Lnet/minecraft/block/ComparatorBlock;FACING:Lnet/minecraft/state/property/DirectionProperty;"))
    private DirectionProperty fdmc$redirectFacingProperty(){
        return HORIZONTAL_FACING4;
    }
}
