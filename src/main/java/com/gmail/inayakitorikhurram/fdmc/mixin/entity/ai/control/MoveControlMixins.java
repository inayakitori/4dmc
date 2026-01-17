package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.control;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.MoveControl4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {
        "net.minecraft.entity.ai.control.MoveControl",
        "net.minecraft.entity.ai.control.FlightMoveControl",
        "net.minecraft.entity.ai.control.AquaticMoveControl"
})
abstract class MoveControlMixins implements MoveControl4 {
    @Expression("?*? + ?*? + ?*?")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    double moveSidewaysW(double original) {
        Vec4d dv = this.getTargetPos().subtract(this.getEntity().getEntityPos());
        ((SidewaysSpeedW) this.getEntity()).setSidewaysSpeedW(
            (float) (Math.signum(dv.w) * this.getSpeedWithAttribute())
        );
        return dv.lengthSquared();
    }
}

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$PhantomMoveControl")
abstract class PhantomEntity$PhantomMoveControlMixin implements MoveControl4 {
    // TODO fix
}

@Mixin(MoveControl.class)
abstract class MoveControlMixin implements MoveControl4 {
    @Shadow protected double targetX;
    @Shadow protected double targetY;
    @Shadow protected double targetZ;

    @Shadow
    @Final
    protected MobEntity entity;

    @Shadow
    protected double speed;

    @Override
    public Vec4d getTargetPos() {
        return new Vec4d(targetX, targetY, targetZ);
    }

    @Override
    public MobEntity getEntity() {
        return this.entity;
    }

    @Override
    public double getSpeedWithAttribute() {
        return this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
    }
}