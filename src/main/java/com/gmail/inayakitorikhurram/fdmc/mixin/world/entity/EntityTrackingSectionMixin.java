package com.gmail.inayakitorikhurram.fdmc.mixin.world.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityTrackingSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityTrackingSection.class)
public class EntityTrackingSectionMixin {
//    @Redirect(method = "forEach(Lnet/minecraft/util/TypeFilter;Lnet/minecraft/util/math/Box;Lnet/minecraft/util/function/LazyIterationConsumer;)Lnet/minecraft/util/function/LazyIterationConsumer$NextIteration;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;intersects(Lnet/minecraft/util/math/Box;)Z"))
//    private boolean fdmc$getBox4$1(Box entityBox, Box box){
//        if(box instanceof Box4 box4){
//            return box4.intersects(Box4.converted(entityBox));
//        } else {
//            return box.intersects(entityBox);
//        }
//    }
//
//    @Redirect(method = "forEach(Lnet/minecraft/util/math/Box;Lnet/minecraft/util/function/LazyIterationConsumer;)Lnet/minecraft/util/function/LazyIterationConsumer$NextIteration;",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;intersects(Lnet/minecraft/util/math/Box;)Z"))
//    private boolean fdmc$getBox4$2(Box entityBox, Box box){
//        if(box instanceof Box4 box4){
//            return box4.intersects(Box4.converted(entityBox));
//        } else {
//            return box.intersects(entityBox);
//        }
//    }
}
