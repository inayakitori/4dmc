package com.gmail.inayakitorikhurram.fdmc.mixin.client.network;

import com.gmail.inayakitorikhurram.fdmc.math.Vec3f;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements CanPlaceW, SidewaysSpeedW {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(
        method = "getDirectionalMovementSpeedMultiplier",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F", ordinal = 0)
    )
    private static float fdmc$considerBothHorizontalMovementSpeeds(float x, @Local(argsOnly = true) Vec2f vec){
        return new Vec2f(x, Vec3f.of(vec).z).length();
    }

    @Inject(
        method = "tickMovementInput",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;sidewaysSpeed:F",
            shift = At.Shift.AFTER,
            opcode = Opcodes.PUTFIELD
        )
    )
    void fdmc$setSidewaysSpeedW(CallbackInfo ci, @Local Vec2f movementSpeed){
        setSidewaysSpeedW(Vec3f.of(movementSpeed).z);
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D", ordinal = 0))
    double move$before$measureX4(ClientPlayerEntity entity, @Share("posW") LocalDoubleRef beforeW){
        Vec4d pos = Vec4d.of(entity.getEntityPos());
        beforeW.set(pos.w);
        return pos.x4;
    }
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D", ordinal = 1))
    double move$after$measureX4(ClientPlayerEntity entity, @Share("posW") LocalDoubleRef beforeW){
        Vec4d pos = Vec4d.of(entity.getEntityPos());
        beforeW.set(pos.w - beforeW.get()); // Now contains dw
        return pos.x4;
    }
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;hypot(FF)F"))
    float move$after$addDistanceMoved4(float dx, float dz, @Share("posW") LocalDoubleRef dw){
        return (float) new Vec3d(dx, dz, dw.get()).length();
    }
}
