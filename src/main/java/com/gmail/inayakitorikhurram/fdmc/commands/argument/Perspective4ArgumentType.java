package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Perspective4ArgumentType implements ArgumentType<Perspective4Argument> {
    public static Perspective4ArgumentType perspective4() {
        return new Perspective4ArgumentType();
    }

    @Override
    public Perspective4Argument parse(StringReader reader) throws CommandSyntaxException {
        return Perspective4Argument.parse(reader);
    }

    public static Perspective4 getPerspective4(CommandContext<ServerCommandSource> context, String name){
        return context.getArgument(name, Perspective4Argument.class).perspective4();
    }

    @Override
    //TODO
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    @Override
    //TODO
    public Collection<String> getExamples() {
        return List.of(Perspective4.DEFAULT.toString());
    }

}
