package com.gmail.inayakitorikhurram.fdmc.mixin.data.client;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;


//there are so many places values() is used here @-@
@Mixin(BlockStateVariantMap.class)
public class BlockStateVariantMapMixin {

    //TODO include not just TripleProprty
    @Mixin(BlockStateVariantMap.TripleProperty.class)
    public static class PropertyMixin{

        @Redirect(method = "*", at= @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/Collection;"))
        private Collection modifyGetValues(Property property){
            return Property4.getValues(property);
        }

    }

}
