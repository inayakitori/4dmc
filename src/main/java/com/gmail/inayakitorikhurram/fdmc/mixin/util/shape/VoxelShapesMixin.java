package com.gmail.inayakitorikhurram.fdmc.mixin.util.shape;

import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(VoxelShapes.class)
public class VoxelShapesMixin {

    @Inject(method = {
            "createHorizontalFacingShapeMap(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Map;",
            "createFacingShapeMap(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Map;"
    },
    at = @At(value = "RETURN"), cancellable = true)
    private static void fdmc$createHorizontalFacingShapeMap(VoxelShape shape, Vec3d anchor, CallbackInfoReturnable<Map<Direction, VoxelShape>> cir){
        cir.setReturnValue(MixinUtil.expandDirectionMapWith(cir.getReturnValue(), VoxelShapes.empty()));
    }

    @Inject(method = {
            "createAxisShapeMap(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Map;",
            "createHorizontalAxisShapeMap(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Map;",
    },
            at = @At(value = "RETURN"), cancellable = true)
    private static void fdmc$createHorizontalAxisShapeMap(VoxelShape shape, Vec3d anchor, CallbackInfoReturnable<Map<Direction.Axis, VoxelShape>> cir){
        cir.setReturnValue(MixinUtil.expandAxisMapWith(cir.getReturnValue(), MixinUtil.constructWFacingVoxelShape(shape, Direction.Axis.Z)));
    }
}
