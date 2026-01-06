package com.gmail.inayakitorikhurram.fdmc.commands;

import com.gmail.inayakitorikhurram.fdmc.commands.argument.Perspective4ArgumentType;
import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
public class FDMCCommands {

    private static final String TARGETS = "targets";
    private static final String PERSPECTIVE4 = "perspective4";
    private static final String TARGET = "target";

    public static void registerAll(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(perspective4());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> perspective4() {
        return
            literal("perspective4")
                .then(argument(TARGETS, EntityArgumentType.entities())
                    .then(getPerspective4())
                    .then(setPerspective4())
                    .then(rotatePerspective4())
                    .then(resetPerspective4())
                );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getPerspective4() {
        return literal("get")
            .executes(context ->
                forEveryEntity(context, entity -> {
                    Perspective4 p4 = ((Perspective4Access) entity).getPerspective4();
                    context.getSource().sendFeedback(() ->
                            (entity.getDisplayName()).copy()
                                    .append(": ")
                                    .append(p4.toString())
                            , false
                    );
                    return Command.SINGLE_SUCCESS;
                })
            );

    }

    private static LiteralArgumentBuilder<ServerCommandSource> setPerspective4() {
        return literal("set")
                .then(argument(PERSPECTIVE4, Perspective4ArgumentType.perspective4())
                    .executes(context ->
                            forEveryEntity(context, entity -> {
                                Perspective4 p4Current = ((Perspective4Access) entity).getPerspective4();
                                Perspective4 p4New = Perspective4ArgumentType.getPerspective4(context, PERSPECTIVE4);
                                if(p4Current.equals(p4New)) return 0;
                                ((Perspective4Access) entity).setPerspective4(p4New);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> rotatePerspective4() {
        return literal("rotate").executes(context -> {
            return 0;
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> resetPerspective4() {
        return literal("reset")
                .executes(context ->
                        forEveryEntity(context, entity -> {
                            Perspective4 p4 = ((Perspective4Access) entity).getPerspective4();
                            if(p4.equals(Perspective4.DEFAULT)){
                                return 0;
                            } else {
                                ((Perspective4Access) entity).setPerspective4(Perspective4.DEFAULT);
                                return Command.SINGLE_SUCCESS;
                            }
                        })
                );
    }

    private static int forEveryEntity(CommandContext<ServerCommandSource> context, Function<Entity,Integer> fn) throws CommandSyntaxException{
        int i = 0;
        for (Entity entity : EntityArgumentType.getEntities(context, TARGETS)) {
            i += fn.apply(entity);
        }
        return i;
    }

}
