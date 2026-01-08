package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MoveControl.class)
class MoveControlMixin {
}


@Mixin(FlightMoveControl.class)
class FlightMoveControlMixin extends MoveControl{
    public FlightMoveControlMixin(MobEntity entity) {
        super(entity);
    }
}
