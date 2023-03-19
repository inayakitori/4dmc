package com.gmail.inayakitorikhurram.fdmc;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDMCConstants {

    //gamerules
    public static GameRules.Key<GameRules.BooleanRule> FLUID_SCALE_W =
            GameRuleRegistry.register(
				"wFluidFlow",
                GameRules.Category.UPDATES,
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

    //buttons

    public static final float BUTTON_MARGIN = 0.0f;
    public static final VoxelShape CEILING_W_SHAPE = Block.createCuboidShape(
             5.0 - BUTTON_MARGIN,  14.0 - BUTTON_MARGIN,  5.0 - BUTTON_MARGIN,
            11.0 + BUTTON_MARGIN,  16.0 + BUTTON_MARGIN, 11.0 + BUTTON_MARGIN);
    public static final VoxelShape CEILING_W_PRESSED_SHAPE = Block.createCuboidShape(
             5.0- BUTTON_MARGIN, 15.0- BUTTON_MARGIN,   5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 16.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape KATA_SHAPE = Block.createCuboidShape(
             5.0- BUTTON_MARGIN,  6.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 10.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape KATA_PRESSED_SHAPE =  Block.createCuboidShape(
             5.0- BUTTON_MARGIN, 7.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 9.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape ANA_SHAPE = Block.createCuboidShape(
             5.0- BUTTON_MARGIN,  6.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 10.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape ANA_PRESSED_SHAPE = Block.createCuboidShape(
             5.0- BUTTON_MARGIN, 7.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 9.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape FLOOR_W_SHAPE = Block.createCuboidShape(
             5.0- BUTTON_MARGIN, 0.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 2.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    public static final VoxelShape FLOOR_W_PRESSED_SHAPE = Block.createCuboidShape(
             5.0, 0.0,  5.0,
            11.0, 1.0, 11.0);

    //chests

    protected static final VoxelShape DOUBLE_KATA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_ANA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

}
