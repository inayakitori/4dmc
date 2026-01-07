package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.HashSet;

public record Perspective4Argument(DirectionArgument d1, DirectionArgument d2, DirectionArgument d3, DirectionArgument d4) {
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION =
            new SimpleCommandExceptionType(Text.of("Incomplete (expected 4 directions)"));
    public static final SimpleCommandExceptionType INVALID_PERSPECTIVE =
            new SimpleCommandExceptionType(Text.of("Expected 4 orthogonal directions"));
    public static Perspective4Argument parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        HashSet<Direction.Axis> setAxes = new HashSet<>();
        DirectionArgument d1 = tryGetNextDirectionArgument(reader, setAxes);
        DirectionArgument d2 = tryGetNextDirectionArgument(reader, setAxes);
        DirectionArgument d3 = tryGetNextDirectionArgument(reader, setAxes);
        DirectionArgument d4 = DirectionArgument.parse(reader);

        if(!Perspective4.validate(d1.direction(), d2.direction(), d3.direction(), d4.direction())){
            reader.setCursor(i);
            throw INVALID_PERSPECTIVE.createWithContext(reader);
        }
        return new Perspective4Argument(d1, d2, d3, d4);
    }

    public Perspective4 perspective4(){
//        if(!Perspective4.validate(d1.direction(), d2.direction(), d3.direction(), d4.direction())){
//            throw INVALID_PERSPECTIVE.createWithContext(reader);
//        }
        return Perspective4.fromDirections(d1.direction(), d2.direction(), d3.direction(), d4.direction());
    }

    private static DirectionArgument tryGetNextDirectionArgument(StringReader reader, HashSet<Direction.Axis> setAxes) throws CommandSyntaxException {
        int i = reader.getCursor();
        int count = setAxes.size();
        DirectionArgument dir = DirectionArgument.parse(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
        reader.skip();
        setAxes.add(dir.direction().getAxis());
        if(setAxes.size() != count + 1){
            reader.setCursor(i);
            throw INVALID_PERSPECTIVE.createWithContext(reader);
        }
        return dir;
    }
}
