package com.gmail.inayakitorikhurram.fdmc.mixin.world.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.SectionedEntityCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionedEntityCache.class)
public class SectionedEntityCacheMixin {

    @Inject(method = "forEachInBox", at = @At("RETURN"))
    private void storeWValues(Box box, LazyIterationConsumer<EntityTrackingSection> consumer, CallbackInfo ci){

    }


    @WrapMethod(method = "forEachInBox")
    private void fdmc$forEachInBoxW(Box box, LazyIterationConsumer<EntityTrackingSection> consumer, Operation<Void> forEachInBox){
        if(box instanceof Box4 box4) {
            int minW = (int) Math.floor(box4.minW);
            int maxW = (int) Math.ceil(box4.maxW);
            //FDMCConstants.LOGGER.info("checking box4 slices between {} <= w < {}", minW, maxW);

            for (int w = minW; w < maxW; w++) {
                // This function only looks at the
                Box newBox = box4.getSlice(w);
                //FDMCConstants.LOGGER.info("checking box4 slice w={}: {}", w, newBox);
                forEachInBox.call(newBox, consumer);
            }
        }
        else {
            forEachInBox.call(box, consumer);
        }
    }

}
