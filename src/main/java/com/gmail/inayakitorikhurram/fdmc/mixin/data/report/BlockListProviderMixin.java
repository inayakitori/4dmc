package com.gmail.inayakitorikhurram.fdmc.mixin.data.report;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockListProvider.class)
public abstract class BlockListProviderMixin {
    private static RegistryEntry.Reference<Block> currentBlock = null;

    @Inject(method = "method_57952",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry$Reference;getIdAsString()Ljava/lang/String;", ordinal = 0), require = 1)
    private static void storeCurrentBlock(RegistryOps registryOps, JsonObject jsonObject, RegistryEntry.Reference entry, CallbackInfo ci, @Local(argsOnly = true) RegistryEntry.Reference<Block> block) {
        currentBlock = block;
    }


    @Redirect(method = "method_57952",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/List;"), require = 1)
    private static <T extends Comparable<T>> List<T> provideProperty4(Property<T> instance) {
        return Property4.getValues(instance, currentBlock);
    }
}
