package com.gmail.inayakitorikhurram.fdmc.mixin.util.shape;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VoxelShape.class)
public class VoxelShapeMixin {
    @WrapMethod(method = "calculateMaxDistance(Lnet/minecraft/util/math/AxisCycleDirection;Lnet/minecraft/util/math/Box;D)D")
    double maxDistanceW(AxisCycleDirection axisCycle, Box box, double maxDist, Operation<Double> original){
        if (box instanceof Box4 box4) {
            for (Box slice : box4.slices()) {
                maxDist = original.call(axisCycle, slice, maxDist);
            }
            return maxDist;
        } else {
            return original.call(axisCycle, box, maxDist);
        }
    }

    @WrapMethod(method = "getBoundingBox")
    Box getBoundingBox4(Operation<Box> original) {
	    return Box4.converted(original.call());
    }

    @Redirect(method = "method_1111", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/util/math/Box;"))
    private static Box getBoundingBoxes4(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Box4.converted(new Box(x1, y1, z1, x2, y2, z2));
    }
}
