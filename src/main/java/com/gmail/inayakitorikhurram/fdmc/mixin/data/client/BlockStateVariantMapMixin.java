package com.gmail.inayakitorikhurram.fdmc.mixin.data.client;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;


//there are so many places values() is used here @-@
@Mixin(BlockStateVariantMap.class)
public class BlockStateVariantMapMixin {

    //TODO include not just TripleProperty
    @Mixin(value = {
            BlockStateVariantMap.SingleProperty.class,
            BlockStateVariantMap.DoubleProperty.class,
            BlockStateVariantMap.TripleProperty.class,
            BlockStateVariantMap.QuadrupleProperty.class,
            BlockStateVariantMap.QuintupleProperty.class
    })
    public static class PropertyMixin{

        @Redirect(method = "*", at= @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/List;"), require = 1)
        private List modifyGetValues(Property property){
            return Property4.getValues(property);
        }

    }

}
