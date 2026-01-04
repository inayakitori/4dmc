package com.gmail.inayakitorikhurram.fdmc.mixin.client.network;

import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements CanPlaceW {
    @Shadow @Final protected MinecraftClient client;

    @Shadow
    public abstract float getYaw(float tickProgress);

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public int stepCooldown() {
        return 5;
    }

    /**
     * This is the inverse function of {@link Entity#movementInputToVelocity}.
     * @return Length of the returned vector is inaccurate, because that information is lost with normalization.
     */
    @Unique
    private static Vec3d velocityToMovementInput(Vec3d velocity, float yaw) {
        float f = MathHelper.sin(yaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(yaw * ((float)Math.PI / 180));

        double movementZ = (velocity.z / f * g - velocity.x) / (g * g / f + f);
        return new Vec3d(
            (velocity.z - movementZ * g) / f,
            velocity.y,
            movementZ
        );
    }

    @Inject(
        method = "tickMovementInput",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;renderYaw:F",
            shift = At.Shift.AFTER
        )
    )
    void fdmc$tickMovementInputRotate(CallbackInfo ci) {
        Perspective4 perspective4 = ((GameOptions4) client.options).getPerspective4();
        Vec3d movementInput = new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed);
        Vec3d v = Entity.movementInputToVelocity(
            movementInput,
            1,
            this.getYaw()
        );
        Vec4d velocity4 = perspective4.projectInverse(new Vec4d(
            v.x,
            v.y,
            v.z,
            0d
        ));
        Vec3d movementInputLogical = ClientPlayerEntityMixin
            .velocityToMovementInput(velocity4.flatten(), this.getYaw())
            .normalize()
            .multiply(movementInput.length());
        this.sidewaysSpeed = (float) movementInputLogical.x;
        this.forwardSpeed = (float) movementInputLogical.z;
    }
}
