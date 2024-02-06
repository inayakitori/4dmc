package com.gmail.inayakitorikhurram.fdmc.mixin.state;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.netty.util.internal.UnstableApi;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(State.class)
public abstract class StateMixin {
    @Shadow @Final protected Object owner;

    @Inject(method = "createWithTable(Ljava/util/Map;)V", at = @At(value = "HEAD"), cancellable = true)
    private <T extends Comparable<T>> void createWithTableCheckProperty(Map<Map<Property4<?>, Comparable<?>>, State<?, ?>> states, CallbackInfo ci) {
        ci.cancel();
        if ( ((State<?, ?>) (Object) this).withTable != null) {
            throw new IllegalStateException();
        } else {
            Table<Property<?>, Comparable<?>, State<?, ?>> table = HashBasedTable.create();

            for (Map.Entry<Property<?>, Comparable<?>> propertyComparableEntry : ((State<?, ?>) (Object) this).getEntries().entrySet()) {
                Property<?> property = propertyComparableEntry.getKey();

                for (Comparable<?> t : Property4.getValues(property, owner)) {
                    if (!t.equals(propertyComparableEntry.getValue())) {
                        table.put(property, t, states.get(((State<?, ?>) (Object)  this).toMapWith(property, t)));
                    }
                }
            }

            ((State) (Object) this).withTable = table.isEmpty() ? table : ArrayTable.create(table);
        }
    }


}
