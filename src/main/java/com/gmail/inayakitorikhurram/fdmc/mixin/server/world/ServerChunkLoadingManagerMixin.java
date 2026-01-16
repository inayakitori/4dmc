package com.gmail.inayakitorikhurram.fdmc.mixin.server.world;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerChunkLoadingManager.class)
public class ServerChunkLoadingManagerMixin {
}

@Mixin(targets = "net.minecraft.server.world.ServerChunkLoadingManager$EntityTracker")
class EntityTrackerMixin {


    @Definition(id = "vec3d", local = @Local(type = Vec3d.class))
    @Definition(id = "x", field = "Lnet/minecraft/util/math/Vec3d;x:D")
    @Definition(id = "z", field = "Lnet/minecraft/util/math/Vec3d;z:D")
    @Expression("vec3d.x * vec3d.x + vec3d.z * vec3d.z")
    @ModifyExpressionValue(method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            , at = @At("MIXINEXTRAS:EXPRESSION"))
    private double fdmc$modifySquaredDistance(double original, @Local Vec3d delta3){
        Vec4d delta4 = new Vec4d(delta3);
	    return delta4.multiply(1d, 1d, 1d, 16d).horizontalLengthSquared();
    }

}
