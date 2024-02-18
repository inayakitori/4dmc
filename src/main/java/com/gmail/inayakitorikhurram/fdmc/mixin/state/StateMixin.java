package com.gmail.inayakitorikhurram.fdmc.mixin.state;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(State.class)
public abstract class StateMixin {
    @Shadow @Final protected Object owner;

    @Inject(method = "createWithTable(Ljava/util/Map;)V", at = @At(value = "HEAD"), cancellable = true)
    private <T extends Comparable<T>> void createWithTableCheckProperty(Map<Map<Property4<?>, Comparable<?>>, State<?, ?>> states, CallbackInfo ci) {
        ci.cancel();
        if (((State<?,?>) (Object) this).withTable != null) {
            throw new IllegalStateException();
        } else {
            Table<Property<?>, Comparable<?>, State<?,?>> table = HashBasedTable.create();
            UnmodifiableIterator var3 = ((State<?,?>) (Object) this).entries.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var3.next();
                Property<?> property = (Property)entry.getKey();
                Iterator var6 = Property4.getValues(property, owner).iterator();

                while(var6.hasNext()) {
                    Comparable<?> comparable = (Comparable)var6.next();
                    if (!comparable.equals(entry.getValue())) {
                        table.put(property, comparable, states.get(((State<?,?>) (Object) this).toMapWith(property, comparable)));
                    }
                }
            }

            ((State<?,?>) (Object) this).withTable = (Table)(table.isEmpty() ? table : ArrayTable.create(table));
        }
    }
}
