package com.gmail.inayakitorikhurram.fdmc;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {

    //gamerules
    public static GameRules.Key<GameRules.BooleanRule> FLUID_FLOW_W =
            GameRuleRegistry.register(
				"wFluidFlow",
                GameRules.Category.UPDATES,
                GameRuleFactory.createBooleanRule(false)
            );

    public static GameRules.Key<GameRules.BooleanRule> QUAD_CHESTS =
            GameRuleRegistry.register(
                    "quadChests",
                    GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(false)
            );

    //networking

    public static Identifier MOVING_PLAYER_ID = new Identifier("fdmc:moving_player");
    public static Identifier UPDATE_COLLISION_MOVEMENT = new Identifier("fdmc:update_collision_movement");
    public static Identifier PLAYER_PLACEMENT_DIRECTION_ID = new Identifier("fdmc:player_placing");

    //step constants
    public static int STEP_DISTANCE = 1<<18;
    public static int CHUNK_STEP_DISTANCE = STEP_DISTANCE>>4;
    public static int FDMC_CHUNK_SCALE = 2;
    public static int FDMC_BLOCK_SCALE = FDMC_CHUNK_SCALE<<4;

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
