package com.gmail.inayakitorikhurram.fdmc.util;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.*;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class MixinUtil {
    private static final Pair<VoxelShape, VoxelShape> EMPTY_VOXEL_SHAPE_PAIR = new Pair<>(VoxelShapes.empty(), VoxelShapes.empty());

    @Deprecated
    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier) {
        return modifyPlacementDirection(ctx, defaultValueSupplier, UnaryOperator.identity());
    }

    @Deprecated
    public static Direction modifyPlacementDirection(ItemPlacementContext ctx, Supplier<Direction> defaultValueSupplier, UnaryOperator<Direction> directionModification) {
        return CanStep.of(ctx.getPlayer())
                .flatMap(CanStep::getPlacementDirection4)
                .map(directionModification)
                .orElseGet(defaultValueSupplier);
    }

    public static AbstractBlock.Settings enableAllWCapabilities(AbstractBlock.Settings settings) {
        BlockSettings4.asBlockSettings4(settings).use4DProperties(true).acceptsWNeighbourUpdates(true);
        return settings;
    }

    public static AbstractBlock.Settings enableAllWCapabilitiesAndGetSideW(AbstractBlock.Settings settings) {
        BlockSettings4.asBlockSettings4(settings).use4DProperties(true).acceptsWNeighbourUpdates(true).useGetSideW(true);
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

    public static Item.Settings use4DProperties(Item.Settings settings) {
        ItemSettings4.asItemSettings4(settings).use4DProperties(true);
        return settings;
    }

    public static Item.Settings use4DPropertiesAndGetSideW(Item.Settings settings) {
        ItemSettings4.asItemSettings4(settings).use4DProperties(true).useGetSideW(true);
        return settings;
    }


    /**
     * @param   shape       a {@link VoxelShape} of a block with a {@link net.minecraft.state.property.DirectionProperty DirectionProperty}
     * @param   direction   the {@link Direction} the shape corresponds to
     * @return              a {@link Pair}<{@link VoxelShape},{@link VoxelShape}>, where the left {@link VoxelShape} points toward {@link Direction4Constants#KATA} and the right one points toward {@link Direction4Constants#ANA}
     */
    public static Pair<VoxelShape, VoxelShape> constructWFacingVoxelShapes(VoxelShape shape, Direction direction) {
        if (direction.getAxis() == Direction4Constants.Axis4Constants.W || direction.getAxis() == Direction.Axis.Y) {
            throw new IllegalArgumentException();
        }

        boolean mirror = direction.getDirection() == Direction.AxisDirection.NEGATIVE;
        Direction.Axis orthogonalAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

        if (shape instanceof SimpleVoxelShape) {
            return constructWFacingVoxelShapeFromBoundingBox(shape.getBoundingBox(), orthogonalAxis, mirror);
        } else if (shape instanceof ArrayVoxelShape) {
            return shape.getBoundingBoxes().stream().map(box -> constructWFacingVoxelShapeFromBoundingBox(box, orthogonalAxis, mirror)).reduce((pair1, pair2) -> {
                VoxelShape left1 = pair1.getLeft();
                VoxelShape left2 = pair2.getLeft();
                VoxelShape right1 = pair1.getRight();
                VoxelShape right2 = pair2.getRight();

                return new Pair<>(VoxelShapes.union(left1, left2), VoxelShapes.union(right1, right2));
            }).orElse(EMPTY_VOXEL_SHAPE_PAIR);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static Pair<VoxelShape, VoxelShape> constructWFacingVoxelShapeFromBoundingBox(Box box, Direction.Axis orthogonalAxis, boolean mirror) {
        double minOrthogonal = box.getMin(orthogonalAxis);
        double maxOrthogonal = box.getMax(orthogonalAxis);
        double minY = box.getMin(Direction.Axis.Y);
        double maxY = box.getMax(Direction.Axis.Y);
        double mirroredMinOrthogonal = 1 - maxOrthogonal;
        double mirroredMaxOrthogonal = 1 - minOrthogonal;

        VoxelShape result = VoxelShapes.cuboid(minOrthogonal, minY, minOrthogonal, maxOrthogonal, maxY, maxOrthogonal);
        VoxelShape mirroredResult = VoxelShapes.cuboid(mirroredMinOrthogonal, minY, mirroredMinOrthogonal, mirroredMaxOrthogonal, maxY, mirroredMaxOrthogonal);

        return mirror ? new Pair<>(mirroredResult, result) : new Pair<>(result, mirroredResult);
    }

    public static VoxelShape constructWFacingVoxelShape(VoxelShape shape, Direction.Axis axis) {
        if (axis == Direction4Constants.Axis4Constants.W || axis == Direction.Axis.Y) {
            throw new IllegalArgumentException();
        }

        Direction.Axis orthogonalAxis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

        if (shape instanceof SimpleVoxelShape) {
            return constructWFacingVoxelShapeFromBoundingBox(shape.getBoundingBox(), orthogonalAxis);
        } else if (shape instanceof ArrayVoxelShape) {
            return shape.getBoundingBoxes().stream().map(box -> constructWFacingVoxelShapeFromBoundingBox(box, orthogonalAxis)).reduce(VoxelShapes::union).orElse(VoxelShapes.empty());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static VoxelShape constructWFacingVoxelShapeFromBoundingBox(Box box, Direction.Axis orthogonalAxis) {
        double minOrthogonal = box.getMin(orthogonalAxis);
        double maxOrthogonal = box.getMax(orthogonalAxis);
        double minY = box.getMin(Direction.Axis.Y);
        double maxY = box.getMax(Direction.Axis.Y);

        return VoxelShapes.cuboid(minOrthogonal, minY, minOrthogonal, maxOrthogonal, maxY, maxOrthogonal);
    }
}
