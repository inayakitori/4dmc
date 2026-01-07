package com.gmail.inayakitorikhurram.fdmc.mixin.client.render;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @WrapOperation(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"
        )
    )
    private void fdmc$transformCamera(Camera instance, double x, double y, double z, Operation<Void> original){
        Perspective4 perspective4 = ((Perspective4Access) MinecraftClient.getInstance().getCameraEntity()).getPerspective4();

        Vec4d logicalPos = new Vec4d(x, y, z);
        Vec4d renderPos = perspective4.project(logicalPos);
		Vec3d cameraPos = renderPos.toPos3();

        original.call(instance, cameraPos.x, cameraPos.y, cameraPos.z);
    }
}
