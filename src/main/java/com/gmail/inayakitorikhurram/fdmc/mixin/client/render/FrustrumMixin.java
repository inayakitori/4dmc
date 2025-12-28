package com.gmail.inayakitorikhurram.fdmc.mixin.client.render;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Frustum.class)
public class FrustrumMixin {
    @WrapMethod(method = "isVisible")
    private boolean fdmc$checkWholeBox(Box box, Operation<Boolean> original){
        if(box instanceof Box4 box4){
            return box4.slices().stream().anyMatch(original::call);
        } else{
            return original.call(box);
        }
    }
}
