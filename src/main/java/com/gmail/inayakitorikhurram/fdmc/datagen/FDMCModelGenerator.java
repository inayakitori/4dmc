package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FDMCModelGenerator extends FabricModelProvider {


    public static final HashMap<Block, String> BUTTONS = Maps.newHashMap(Map.of(
            Blocks.STONE_BUTTON,
            "stone",
            Blocks.OAK_BUTTON,
            "oak_planks",
            Blocks.SPRUCE_BUTTON,
            "spruce_planks",
            Blocks.BIRCH_BUTTON,
            "birch_planks",
            Blocks.JUNGLE_BUTTON,
            "jungle_planks",
            Blocks.ACACIA_BUTTON,
            "acacia_planks",
            Blocks.DARK_OAK_BUTTON,
            "dark_oak_planks",
            Blocks.MANGROVE_BUTTON,
            "mangrove_planks",
            Blocks.CRIMSON_BUTTON,
            "crimson_planks"
    ));;

    static {
        BUTTONS.put(
                Blocks.WARPED_BUTTON,
                "warped_planks"
        );
        BUTTONS.put(
                Blocks.POLISHED_BLACKSTONE_BUTTON,
                "polished_blackstone"
        );
        BUTTONS.put(
                Blocks.CHERRY_BUTTON,
                "cherry_planks"
        );
        BUTTONS.put(
                Blocks.BAMBOO_BUTTON,
                "bamboo_planks"
        );
    }


    private static final HashMap<BlockFace, VariantSettings.Rotation> BUTTON_FACE = Maps.newHashMap(ImmutableMap.of(
            BlockFace.FLOOR, VariantSettings.Rotation.R0   ,
            BlockFace.WALL , VariantSettings.Rotation.R90  ,
            BlockFace.CEILING , VariantSettings.Rotation.R180
    ));

    private static final HashMap<Direction, VariantSettings.Rotation> BUTTON_ROTATION = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH , VariantSettings.Rotation.R0   ,
            Direction4Constants.EAST  , VariantSettings.Rotation.R90  ,
            Direction4Constants.SOUTH , VariantSettings.Rotation.R180 ,
            Direction4Constants.WEST  , VariantSettings.Rotation.R270 ,
            Direction4Constants.KATA  , VariantSettings.Rotation.R0   ,
            Direction4Constants.ANA   , VariantSettings.Rotation.R180
    ));

    public FDMCModelGenerator(FabricDataOutput output) {
        super(output);
    }

    /**
     *
     * @param texture block texture
     * @param block button block
     * @param blockStateModelGenerator get this from the generateBlockstatesModel
     */
    private void createButton4(Identifier texture, Block block, BlockStateModelGenerator blockStateModelGenerator){
        TextureMap textureMap = TextureMap.texture(texture);

        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(block);

        blockStateSupplier.coordinate(
                BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_FACE, Properties.POWERED)
                        .register(
                                (facing, face, powered) -> {
                                    boolean isW = facing.getAxis() == Direction4Constants.Axis4Constants.W;
                                    String variant;
                                    if(isW) {
                                        //wall and floor use same model
                                        String face_variant = (face == BlockFace.CEILING ? BlockFace.FLOOR : face).asString();
                                        //variant name
                                        variant = "_" + facing.getName() + "_" + face_variant + (powered ? "_pressed" : "");
                                    } else{
                                        variant = (powered ? "_pressed" : "");
                                    }

                                    //for some reason this gives blocks/[material]_button when the data is under block/[material]_button
                                    Identifier modelId = new Identifier(block.getLootTableId().getPath().replace("blocks", "block") + variant);
                                    Identifier parentId = new Identifier("minecraft", "block/button" + variant);
                                    Model model = new Model(
                                            Optional.of(
                                                    parentId
                                            ),
                                            Optional.of(variant),
                                            TextureKey.TEXTURE
                                    );

                                    //don't reupload floor/ceiling models or mutiple direction models
                                    if(
                                            (facing.getAxis() == Direction4Constants.Axis4Constants.W && face != BlockFace.CEILING) ||
                                            (facing == Direction.EAST && face == BlockFace.WALL)
                                    ) {
                                        model.upload(block, textureMap, blockStateModelGenerator.modelCollector);
                                    }

                                    return BlockStateVariant.create().put(
                                            VariantSettings.X,
                                            //don't rotate w walls ones only x z or floor/ceiling
                                            isW && face == BlockFace.WALL? VariantSettings.Rotation.R0 : BUTTON_FACE.get(face)
                                    ).put(
                                            VariantSettings.Y,
                                            BUTTON_ROTATION.get(facing)
                                    ).put(
                                            VariantSettings.MODEL,
                                            modelId
                                    );
                                }
                        )
        );


        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

    }


    private record ButtonVariants(Direction facing, BlockFace face, boolean powered){

    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //TODO use a json parser to get the texture somehow
        BUTTONS.forEach((block, textureName) -> {
            createButton4(Identifier.of("minecraft", "block/" + textureName), block, blockStateModelGenerator);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //none
    }
}
