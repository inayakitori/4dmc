package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

}
