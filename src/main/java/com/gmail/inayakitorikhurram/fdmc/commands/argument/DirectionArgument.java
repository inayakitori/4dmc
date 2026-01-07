package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record DirectionArgument(Direction direction) {
    public static final SimpleCommandExceptionType MISSING_DIR =
            new SimpleCommandExceptionType(Text.of("Expected a direction"));
    public static final SimpleCommandExceptionType INVALID_DIR =
            new SimpleCommandExceptionType(Text.of("Not a direction"));

    public static final List<String> AXIS_SIGN_IDS = Arrays.stream(Direction4Constants.Axis4Constants.VALUES)
            .map(Direction.Axis::getId)
            .map(axis -> new String[]{axis + "+", axis + "-"})
            .flatMap(Arrays::stream).toList();

    public static final List<String> ALL_IDS = new ArrayList<>(Arrays.stream(Direction4Constants.VALUES)
            .map(Direction::getId).toList());
    static{
        ALL_IDS.addAll(AXIS_SIGN_IDS);
    }

    public static DirectionArgument parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead()){
            throw MISSING_DIR.createWithContext(reader);
        }
        int start = reader.getCursor();
        int i = 0;
        while(reader.canRead() && reader.peek() != ' '){
            reader.skip();
            if(++i > 10) throw INVALID_DIR.createWithContext(reader);
        }
        String potentialDir = reader.getString().substring(start, reader.getCursor()).toLowerCase();

        Direction dir = Direction.CODEC.byId(potentialDir);
        if(dir != null){
            return new DirectionArgument(dir);
        }else {
            reader.setCursor(start);
            return parseAxisSign(reader);
        }
    }

    private static DirectionArgument parseAxisSign(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        Direction.Axis axis = Direction.Axis.CODEC.byId("" + reader.read());
        if(axis == null){
            reader.setCursor(start);
            throw INVALID_DIR.createWithContext(reader);
        }
        if(!reader.canRead()){
            reader.setCursor(start);
            throw INVALID_DIR.createWithContext(reader);
        }
        char sign = Character.toLowerCase(reader.read());
        // the stream should either terminate or have a space next
        if(reader.canRead() && reader.peek() != ' '){
            reader.setCursor(start);
            throw INVALID_DIR.createWithContext(reader);
        }
        if(sign == '+'){
            return new DirectionArgument(axis.getPositiveDirection());
        } else if(sign == '-'){
            return new DirectionArgument(axis.getNegativeDirection());
        }
        reader.setCursor(start);
        throw INVALID_DIR.createWithContext(reader);
    }
}
