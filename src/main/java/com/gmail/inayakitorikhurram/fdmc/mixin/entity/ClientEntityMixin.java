package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ClientEntityMixin {
    @Inject(
        method = "getCameraPosVec",
        at = @At("RETURN"),
        cancellable = true,
        order = 1000-7
    )
    private void fdmc$returnCameraBackForCrosshairTarget(float tickDelta, CallbackInfoReturnable<Vec3d> cir){
        if((Entity)(Object) this instanceof ClientPlayerEntity) {
            Perspective4 perspective4 = ((Perspective4Access) this).getPerspective4();

            Vec4d logicalPos = new Vec4d(cir.getReturnValue());
            Vec4d renderPos = perspective4.project(logicalPos);
            cir.setReturnValue(renderPos.toPos3());
        }
    }
}
