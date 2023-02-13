package com.gmail.inayakitorikhurram.fdmc.mixin.data.report;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.data.DataWriter;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

@Mixin(BlockListProvider.class)
public abstract class BlockListProviderMixin {
    private Block currentBlock = null;

    @Inject(method = "run(Lnet/minecraft/data/DataWriter;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/DefaultedRegistry;getId(Ljava/lang/Object;)Lnet/minecraft/util/Identifier;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeCurrentBlock(DataWriter writer, CallbackInfoReturnable<CompletableFuture<?>> cir, JsonObject jsonObject, Iterator var3, Block block) {
        currentBlock = block;
    }


    @Redirect(method = "run(Lnet/minecraft/data/DataWriter;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/Collection;"))
    private <T extends Comparable<T>> Collection<T> provideProperty4(Property<T> instance) {
        return Property4.getValues(instance, currentBlock);
    }
}
