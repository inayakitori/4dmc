package com.gmail.inayakitorikhurram.fdmc.util;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class MixinUtil {

    //if all horizontal3 sides are solid, then so are the kata/ana sides
    public static boolean areWSidesSolidFullSquares(BlockView world, BlockPos pos){
        for (Direction offsetDir: Direction.HORIZONTAL) {
            if (!world.getBlockState(pos).isSideSolidFullSquare(world, pos, offsetDir)) return false;
        }
        return true;
    }

    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier) {
        return modifyPlacementDirection(ctx, defaultValueSupplier, UnaryOperator.identity());
    }

    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier, UnaryOperator<Direction> directionModification) {
        return CanStep.of(ctx.getPlayer())
                .flatMap(CanStep::getPlacementDirection4)
                .map(directionModification)
                .orElseGet(defaultValueSupplier);
    }

    public static AbstractBlock.Settings enableAll(AbstractBlock.Settings settings) {
        BlockSettings4.asBlockSettings4(settings).use4DProperties(true).acceptsWNeighbourUpdates(true);
        return settings;
    }

    public static AbstractBlock.Settings acceptWNeighbourUpdates(AbstractBlock.Settings settings) {
        BlockSettings4.asBlockSettings4(settings).acceptsWNeighbourUpdates(true);
        return settings;
    }

    public static AbstractBlock.Settings use4DProperties(AbstractBlock.Settings settings) {
        BlockSettings4.asBlockSettings4(settings).use4DProperties(true);
        return settings;
    }

    public static Item.Settings enableAll(Item.Settings settings) {
        ItemSettings4.asItemSettings4(settings).use4DProperties(true);
        return settings;
    }
}
