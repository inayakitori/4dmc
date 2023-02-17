package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScaffoldingBlock.class)
public abstract class ScaffoldingBlockMixin {
    @Redirect(method = "calculateDistance", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"))
    private static Direction.Type calculateWithHorizontal4() {
        return Direction4Constants.Type4.HORIZONTAL4;
    }
}
