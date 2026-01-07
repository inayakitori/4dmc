package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
        String remaining = builder.getRemainingLowerCase();
        String[] segments = remaining.split(" ");
        ArrayList<String> validSegments = new ArrayList<>();
        String finalSegment = segments[segments.length - 1];
        HashSet<Direction.Axis> remainingAxes = new HashSet<>(List.of(Direction4Constants.Axis4Constants.VALUES));
        for (String segment : segments) {
            try {
                remainingAxes.remove(DirectionArgument.parse(new StringReader(segment)).direction().getAxis());
                validSegments.add(segment);
            } catch (CommandSyntaxException e) {
            }
        }
        // If none throw then we need to make it suggest for the empty string instead of the last, correctly parsed one
        if(segments.length == validSegments.size()){
            finalSegment = "";
        }
        ArrayList<String> possibleValues = new ArrayList<>(Arrays.stream(Direction4Constants.VALUES)
                .filter(direction -> remainingAxes.contains(direction.getAxis()))
                .map(Direction::getId)
                .toList());
        List<Character> remainingAxisStarts = remainingAxes.stream()
                .map(Direction.Axis::getId)
                .map(s -> s.charAt(0))
                .toList();
        possibleValues.addAll(
                DirectionArgument.AXIS_SIGN_IDS.stream()
                        // add all the ones which match the remaining axes
                        .filter(s -> remainingAxisStarts.contains(s.charAt(0)))
                        .toList()
        );
        String finalSegment1 = finalSegment;
        possibleValues.stream()
                .filter(s -> s.startsWith(finalSegment1))
                .map(ending -> String.join(" ", Stream.concat(validSegments.stream(), Stream.of(ending)).toList()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    //TODO
    public Collection<String> getExamples() {
        return List.of(Perspective4.DEFAULT.toString());
    }

}
