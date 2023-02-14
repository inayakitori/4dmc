package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {


    public static final Direction4Enum[] UPDATE_ORDER_4 = new Direction4Enum[]{Direction4Enum.WEST, Direction4Enum.EAST, Direction4Enum.DOWN, Direction4Enum.UP, Direction4Enum.NORTH, Direction4Enum.SOUTH, Direction4Enum.KATA, Direction4Enum.ANA};

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static Identifier PLAYER_PLACEMENT_DIRECTION_ID = new Identifier("fdmc:player_placing");
    public static int STEP_DISTANCE = 1<<18;
    public static int CHUNK_STEP_DISTANCE = STEP_DISTANCE>>4;
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;


    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
