package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.getTargetX;
import net.minecraft.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = {
        "net.minecraft.entity.ai.control.MoveControl",
        "net.minecraft.entity.ai.control.FlightMoveControl",
        "net.minecraft.entity.ai.control.AquaticMoveControl"
})
abstract class MoveControlMixins implements getTargetX {
    // TODO fix
}

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$PhantomMoveControl")
abstract class PhantomEntity$PhantomMoveControlMixin implements getTargetX {
    // TODO fix
}

@Mixin(MoveControl.class)
abstract class MoveControlMixin implements getTargetX{
    @Shadow
    protected double targetX;

    @Override
    public double targetX() {
        return this.targetX;
    }
}