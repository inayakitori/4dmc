package com.gmail.inayakitorikhurram.fdmc.mixin.command.argument;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(BlockArgumentParser.class)
public abstract class BlockArgumentParserMixin {
    @Shadow private Identifier blockId;

    @Shadow private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    @Inject(method = "parseBlockProperties()V", at = @At(value = "FIELD", target = "Lnet/minecraft/command/argument/BlockArgumentParser;suggestions:Ljava/util/function/Function;", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void parseBlockProperties4(CallbackInfo ci, int i, String string, Property<?> property) {
        suggestions = builder -> this.fdmcSuggestPropertyValues(builder, property).buildFuture();
    }

    private <T extends Comparable<T>> SuggestionsBuilder fdmcSuggestPropertyValues(SuggestionsBuilder builder, Property<T> property) {
        for (T comparable : Property4.getValues(property, Registries.BLOCK.get(blockId))) {
            if (comparable instanceof Integer) {
                Integer integer = (Integer)comparable;
                builder.suggest(integer);
                continue;
            }
            builder.suggest(property.name(comparable));
        }
        return builder;
    }

    @Redirect(method = "parsePropertyValue(Lnet/minecraft/state/property/Property;Ljava/lang/String;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;parse(Ljava/lang/String;)Ljava/util/Optional;"))
    private <T extends Comparable<T>> Optional<T> parsePropertyValueEnsureProperty4Safety(Property<T> instance, String s) {
        Optional<T> optionalProperty = instance.parse(s);
        if (optionalProperty.isEmpty() || !(instance instanceof Property4)) {
            return optionalProperty;
        } else if (Property4.getValues(instance, Registries.BLOCK.get(blockId)).contains(optionalProperty.get())) {
            return optionalProperty;
        } else {
            return Optional.empty();
        }

    }
}
