package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {


    public static final Direction4[] UPDATE_ORDER_4 = new Direction4[]{Direction4.WEST, Direction4.EAST, Direction4.DOWN, Direction4.UP, Direction4.NORTH, Direction4.SOUTH, Direction4.KATA, Direction4.ANA};

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static Identifier PLAYER_PLACEMENT_DIRECTION_ID = new Identifier("fdmc:player_placing");
    public static int STEP_DISTANCE = 1<<18;
    public static int CHUNK_STEP_DISTANCE = STEP_DISTANCE>>4;
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;


    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
