package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;

@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {


    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH, Block.createCuboidShape( 5.5, 3.0, 11.0, 10.5, 13.0, 16.0),
            Direction4Constants.SOUTH, Block.createCuboidShape( 5.5, 3.0,  0.0, 10.5, 13.0, 5.0 ),
            Direction4Constants.WEST , Block.createCuboidShape(11.0, 3.0,  5.5, 16.0, 13.0, 10.5),
            Direction4Constants.EAST , Block.createCuboidShape( 0.0, 3.0,  5.5,  5.0, 13.0, 10.5),
            Direction4Constants.KATA , Block.createCuboidShape(11.0, 3.0,  5.5, 16.0, 13.0, 10.5),//todo change these
            Direction4Constants.ANA  , Block.createCuboidShape( 0.0, 3.0,  5.5,  5.0, 13.0, 10.5)
    ));


}