package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Box.class)
public class BoxMixin {
    @WrapMethod(method = "intersection")
    private Box fdmc$intersection(Box box, Operation<Box> this$intersection){
        if(box instanceof Box4 box4){
            return box4.intersection((Box)(Object)this);
        } else {
            return this$intersection.call(box);
        }
    }

    @WrapMethod(method = "intersects(Lnet/minecraft/util/math/Box;)Z")
    private boolean fdmc$intersects(Box box, Operation<Boolean> this$intersects){
        if(box instanceof Box4 box4){
            return box4.intersects((Box)(Object)this);
        } else {
            return this$intersects.call(box);
        }
    }
}
