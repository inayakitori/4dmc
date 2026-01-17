package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {
    public static final ScreenHandlerType<FDMCScreenHandler> GENERIC_9X12 = ScreenHandlerType.register("generic_9x12", FDMCScreenHandler::createGeneric9x12);
    public static final float STEP_HUNGER_MULTIPLIER = 1f;
    public static final double ENTITY_RENDER_MAX_DW = 3d;

    //gamerules

    //TODO readd

//    public static GameRules.Key<GameRules.BooleanRule> FLUID_FLOW_W =
//            GameRuleRegistry.register(
//				"wFluidFlow",
//                GameRules.Category.UPDATES,
//                GameRuleFactory.createBooleanRule(false)
//            );

//    public static GameRules.Key<GameRules.BooleanRule> QUAD_CHESTS =
//            GameRuleRegistry.register(
//                    "quadChests",
//                    GameRules.Category.MISC,
//                    GameRuleFactory.createBooleanRule(false)
//            );


    //step constants
    public static int STEP_DISTANCE_BITS = 18;
    public static int STEP_DISTANCE = 1<<STEP_DISTANCE_BITS;
    public static int MAX_SLICE = -8 + (int) Math.floor(29999984f/STEP_DISTANCE);
    public static int MAX_SLICE_NETHER = (int) Math.floor(MAX_SLICE/8f);
    public static int CHUNK_STEP_DISTANCE_BITS = STEP_DISTANCE_BITS-4;
    public static int CHUNK_STEP_DISTANCE = 1<<CHUNK_STEP_DISTANCE_BITS;
    public static int FDMC_CHUNK_SCALE = 1;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;
    public static int FDMC_CAVE_SCALE = 1;

    public static final int INITIAL_VIEW_DISTANCE = 3;

    public static final float PATHFINDING_W_SCALE = 1.5f;
    public static final float FOLLOW_RANGE_W_SCALE = 3.5f;
    public static final int RAYCAST_THICKNESS = 3;

    //worldgen
    public static final int BIOMESCALEW = 64; // a step in W travels how many biome blocks?
    public static final float BIOME_W_WEIGHT = 0.6f;
    public static final float BIOME_XYZ_WEIGHT = 0.8f;

    //stats
    public static final Identifier STAT_STEP_COUNT = Identifier.of("fdmc", "step_count");

    //logging
    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

    //screenshots
    public static final String FDMC_TEMP_FOLDER = "4dmc_temp";
}
