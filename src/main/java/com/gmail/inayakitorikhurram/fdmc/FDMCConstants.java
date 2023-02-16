package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {


    //public static final Direction4Enum[] UPDATE_ORDER_4 = new Direction4Enum[]{Direction4Enum.WEST, Direction4Enum.EAST, Direction4Enum.DOWN, Direction4Enum.UP, Direction4Enum.NORTH, Direction4Enum.SOUTH, Direction4Enum.KATA, Direction4Enum.ANA};

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static Identifier PLAYER_PLACEMENT_DIRECTION_ID = new Identifier("fdmc:player_placing");
    public static int STEP_DISTANCE = 1<<18;
    public static int CHUNK_STEP_DISTANCE = STEP_DISTANCE>>4;
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;

    //buttons
    public static final VoxelShape CEILING_W_SHAPE = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
    public static final VoxelShape KATA_PRESSED_SHAPE = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    public static final VoxelShape ANA_PRESSED_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    public static final VoxelShape FLOOR_W_PRESSED_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);


    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
