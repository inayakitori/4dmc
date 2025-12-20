package com.gmail.inayakitorikhurram.fdmc.mixin.predicate.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.DistancePredicateI;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function5;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DistancePredicate.class)
public class DistancePredicateMixin implements DistancePredicateI {



    @Redirect(method = "method_53118", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P5;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function5;)Lcom/mojang/datafixers/kinds/App;"))
    private static <T1 extends K1,T2,T3,T4,T5, F extends K1,R> App<F, R> fdmc$addWToCodec(Products.P5 instanceGroup, Applicative<F, ?> instance, Function5<T1, T2, T3, T4, T5, R> distancePredicateCreator){
        //noinspection unchecked SILENCE
        return instanceGroup.and(
                NumberRange.DoubleRange.CODEC
                        .optionalFieldOf("w", NumberRange.DoubleRange.ANY)
                        .forGetter(o -> ((DistancePredicateI)(Object)o).getW())
        ).apply(instance, (x, y, z, h, a, w) ->
                DistancePredicateI.create((NumberRange.DoubleRange) x, (NumberRange.DoubleRange) y, (NumberRange.DoubleRange) z, (NumberRange.DoubleRange) w, (NumberRange.DoubleRange) h, (NumberRange.DoubleRange) a)
        );

    }

    @Unique
    NumberRange.DoubleRange w;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void fdmc$initialiseWBounds(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z, NumberRange.DoubleRange horizontal, NumberRange.DoubleRange absolute, CallbackInfo ci){
        this.w = NumberRange.DoubleRange.ANY;
    }

    //right now this just ignores the W value
    @WrapMethod(method = "test")
    private boolean modifiedTest(double x03, double y0, double z0, double x13, double y1, double z1, Operation<Boolean> this$test){

        double[] xw0 = FDMCMath.splitX3(x03);
        double x0 = xw0[0];
        double w0 = xw0[1];
        double[] xw1 = FDMCMath.splitX3(x13);
        double x1 = xw1[0];
        double w1 = xw1[1];

        double dw = w0 - w1;

        if (!this$test.call(x0, y0, z0, x1, y1, z1)) {
            return false;
        }

        FDMCConstants.LOGGER.info("passed check in wd, w values {}, {}", w0, w1);

        return w.test(MathHelper.abs((float) dw));
    }

    @Override
    public NumberRange.DoubleRange getW() {
        return w;
    }

    @Override
    public void setW(NumberRange.DoubleRange w) {
        this.w = w;
    }
}
