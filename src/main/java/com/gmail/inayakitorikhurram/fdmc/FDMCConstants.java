package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {

    public static final float HUE_NEITHER = 0;
    public static final float HUE_KATA = 0;
    public static final float HUE_ANA = 0;
    public static final float HUE_BOTH = 0;

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static int STEP_DISTANCE = 1<<18;
    public static int CHUNK_STEP_DISTANCE = STEP_DISTANCE>>4;
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
