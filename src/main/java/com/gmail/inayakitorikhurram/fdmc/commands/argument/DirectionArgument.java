package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public record DirectionArgument(Direction direction) {
    public static final SimpleCommandExceptionType MISSING_DIR =
            new SimpleCommandExceptionType(Text.of("Expected a direction"));
    public static final SimpleCommandExceptionType INVALID_DIR =
            new SimpleCommandExceptionType(Text.of("Not a direction"));
    public static DirectionArgument parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead()){
            throw MISSING_DIR.createWithContext(reader);
        }
        int start = reader.getCursor();
        int i = 0;
        while(reader.canRead() && reader.read() != ' '){
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
        char sign = reader.read();
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
