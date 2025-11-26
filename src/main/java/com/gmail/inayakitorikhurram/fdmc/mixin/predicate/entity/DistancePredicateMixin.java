package com.gmail.inayakitorikhurram.fdmc.mixin.predicate.entity;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.predicate.entity.DistancePredicate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DistancePredicate.class)
public class DistancePredicateMixin{

    //right now this just ignores the W value
    @WrapMethod(method = "test")
    private boolean modifiedTest(double x03, double y0, double z0, double x13, double y1, double z1, Operation<Boolean> this$test){

        double[] xw0 = FDMCMath.splitX3(x03);
        double x0 = xw0[0];
        double w0 = xw0[0];
        double[] xw1 = FDMCMath.splitX3(x13);
        double x1 = xw1[0];
        double w1 = xw1[0];


        return this$test.call(x0, y0, z0, x1, y1, z1);
    }
}
