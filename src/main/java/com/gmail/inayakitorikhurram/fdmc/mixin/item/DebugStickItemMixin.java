package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.DebugStickItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugStickItem.class)
public abstract class DebugStickItemMixin {

    @Shadow
    private static <T> T cycle(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return null;
    }


    //if the block uses 4D properties and the property itself is 4d then use all possible values
    @Inject(method = "cycle(Lnet/minecraft/block/BlockState;Lnet/minecraft/state/property/Property;Z)Lnet/minecraft/block/BlockState;",
            at = @At("HEAD"), cancellable = true)
    private static void modifiedPropertyCycle(BlockState state, Property property, boolean inverse, CallbackInfoReturnable<BlockState> cir){
        if ( ((BlockSettings4Access)state.getBlock()).uses4DProperties() &&
                property instanceof Property4<?> property4
        ) {
            cir.setReturnValue(state.with(
                    property,
                    (Comparable) cycle(
                            (Iterable) property4.getValues4(),
                            (Object) state.get(property),
                            inverse
                    )
            ));
        }
    }

}
