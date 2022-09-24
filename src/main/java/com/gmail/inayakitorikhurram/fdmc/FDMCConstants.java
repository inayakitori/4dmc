package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static int STEP_DISTANCE = 4096;
    public static int CHUNK_STEP_DISTANCE = (int)Math.floor(STEP_DISTANCE/16f);
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE * 16;

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");


}
