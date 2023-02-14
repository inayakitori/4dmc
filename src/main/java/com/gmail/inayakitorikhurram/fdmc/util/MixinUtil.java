package com.gmail.inayakitorikhurram.fdmc.util;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class MixinUtil {
    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier) {
        return modifyPlacementDirection(ctx, defaultValueSupplier, UnaryOperator.identity());
    }

    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier, UnaryOperator<Direction> directionModification) {
        return Optional.ofNullable(ctx.getPlayer())
                .map(CanStep.class::cast)
                .flatMap(CanStep::getPlacementDirection4)
                .map(Direction.class::cast)
                .map(directionModification)
                .orElseGet(defaultValueSupplier);
    }
}
