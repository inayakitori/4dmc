package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier MOVED_PLAYER_ID = new Identifier("fdmc:moved_player");
    public static Identifier REQUEST_COLLISION_CHECK = new Identifier("fdmc:request_collision_check");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static int STEP_DISTANCE = 4096;
    public static int CHUNK_STEP_DISTANCE = (int)Math.floor(STEP_DISTANCE/16f);
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE * 16;

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");


}
