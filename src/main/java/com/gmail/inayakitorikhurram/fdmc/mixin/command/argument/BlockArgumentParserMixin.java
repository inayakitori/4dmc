package com.gmail.inayakitorikhurram.fdmc.mixin.command.argument;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockArgumentParser.class)
public class BlockArgumentParserMixin {
    @Shadow private Identifier blockId;

    @SuppressWarnings("unchecked")
    @Redirect(method = "suggestTagPropertyValues(Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/BlockArgumentParser;suggestPropertyValues(Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;Lnet/minecraft/state/property/Property;)Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;"))
    private <T extends Comparable<T>> SuggestionsBuilder redirectSuggestTagPropertyValues(SuggestionsBuilder builder, Property<T> property) {
        for (T comparable : Property4.getValues(property, Registries.BLOCK.get(blockId))) {
            if (comparable instanceof Integer) {
                Integer integer = (Integer) comparable;
                builder.suggest(integer);
                continue;
            }
            builder.suggest(property.name(comparable));
        }
        return builder;
    }
}
