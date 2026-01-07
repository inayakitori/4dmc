package com.gmail.inayakitorikhurram.fdmc.commands.argument;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.gmail.inayakitorikhurram.fdmc.commands.argument.DirectionArgument.AXIS_SIGN_IDS;

class DirectionArgumentTest {

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void parse() {
        Assertions.assertArrayEquals(
                Direction4Constants.VALUES,
                Arrays.stream(Direction4Constants.VALUES)
                        .map(Direction::getId)
                        .peek(s -> Assertions.assertEquals(s.toLowerCase(), s))
                        .map(StringReader::new)
                        .map(reader -> {
                            try { // y no nice syntax :C
                                return DirectionArgument.parse(reader);
                            } catch (CommandSyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(DirectionArgument::direction)
                        .toArray()
        );

        Assertions.assertArrayEquals(
                Arrays.stream(Direction4Constants.Axis4Constants.VALUES)
                        .map(Direction.Axis::getDirections)
                        .flatMap(Arrays::stream)
                        .toArray()
                ,
                AXIS_SIGN_IDS.stream()
                        .peek(s -> Assertions.assertEquals(s.toLowerCase(), s))
                        .map(StringReader::new)
                        .map(reader -> {
                            try { // y no nice syntax :C
                                return DirectionArgument.parse(reader);
                            } catch (CommandSyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(DirectionArgument::direction)
                        .toArray()
        );


    }
}