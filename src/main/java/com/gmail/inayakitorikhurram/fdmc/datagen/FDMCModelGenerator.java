package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.W_INDICATOR;
import static net.minecraft.data.client.VariantSettings.Rotation.*;


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


    private static final HashMap<WallMountLocation, VariantSettings.Rotation> BUTTON_FACE = Maps.newHashMap(ImmutableMap.of(
            WallMountLocation.FLOOR, R0   ,
            WallMountLocation.WALL , R90  ,
            WallMountLocation.CEILING , R180
    ));

    private static final HashMap<Direction, VariantSettings.Rotation> BUTTON_ROTATION = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH , R0   ,
            Direction4Constants.EAST  , R90  ,
            Direction4Constants.SOUTH , R180 ,
            Direction4Constants.WEST  , VariantSettings.Rotation.R270 ,
            Direction4Constants.KATA  , R0   ,
            Direction4Constants.ANA   , R180
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
                BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.WALL_MOUNT_LOCATION, Properties.POWERED)
                        .register(
                                (facing, face, powered) -> {
                                    boolean isW = facing.getAxis() == Direction4Constants.Axis4Constants.W;
                                    String variant;
                                    if(isW) {
                                        //wall and floor use same model
                                        String face_variant = (face == WallMountLocation.CEILING ? WallMountLocation.FLOOR : face).asString();
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
                                            (facing.getAxis() == Direction4Constants.Axis4Constants.W && face != WallMountLocation.CEILING) ||
                                            (facing == Direction.EAST && face == WallMountLocation.WALL)
                                    ) {
                                        model.upload(block, textureMap, blockStateModelGenerator.modelCollector);
                                    }

                                    return BlockStateVariant.create().put(
                                            VariantSettings.X,
                                            //don't rotate w walls ones only x z or floor/ceiling
                                            isW && face == WallMountLocation.WALL? R0 : BUTTON_FACE.get(face)
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
    private static final HashMap<Direction, VariantSettings.Rotation[]> PISTON_ROTATION = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH , new VariantSettings.Rotation[]{null , null},
            Direction4Constants.EAST  , new VariantSettings.Rotation[]{null , R90 },
            Direction4Constants.SOUTH , new VariantSettings.Rotation[]{null , R180},
            Direction4Constants.WEST  , new VariantSettings.Rotation[]{null , R270},
            Direction4Constants.KATA  , new VariantSettings.Rotation[]{null , null},
            Direction4Constants.ANA   , new VariantSettings.Rotation[]{null , null},
            Direction4Constants.UP    , new VariantSettings.Rotation[]{R270 , null},
            Direction4Constants.DOWN  , new VariantSettings.Rotation[]{R90  , null}
    ));

    private void createPiston(boolean sticky, BlockStateModelGenerator blockStateModelGenerator){
        Block piston = sticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(piston);

        blockStateSupplier.coordinate(BlockStateVariantMap.create(Properties.FACING, Properties.EXTENDED).register(
            (facing, extended) -> {
                boolean isW = facing.getAxis() == Direction4Constants.Axis4Constants.W;
                ParentIdTracker idTracker = new ParentIdTracker("piston");
                TextureMap texture = new TextureMap();
                boolean saveModel = false;

                if(isW){
                    saveModel = true;
                    idTracker.append("_w");
                }

                if (extended) {
                    idTracker.append("_base");
                    if(sticky){
                        saveModel = false;
                    }
                } else {
                    if(sticky) {
                        idTracker.prepend("sticky_");
                        texture.put(TextureKey.TOP, Identifier.of("minecraft","block/piston_top_sticky"));
                    }
                }

                if(facing == Direction4Constants.ANA){
                    idTracker.append("_ana");
                    texture.put(W_INDICATOR, Identifier.of("fdmc", "block/piston_ana"));
                } else if(facing == Direction4Constants.KATA){
                    idTracker.append("_kata");
                    texture.put(W_INDICATOR, Identifier.of("fdmc", "block/piston_kata"));
                }
                //model
                //for some reason this gives blocks/[material]_button when the data is under block/[material]_button
                String namespace = "minecraft";
                Identifier modelId = new Identifier(namespace, "block/" + idTracker.getVariant());
                if(isW) {
                    namespace = "fdmc";
                    modelId = new Identifier(namespace, "block/piston/" + idTracker.getVariant());
                }
                if(saveModel) {
                    Identifier parentId = new Identifier(namespace, "block/piston/" + idTracker.getParent());
                    LOGGER.info("parent id: {} variant id: {}", parentId, modelId);
                    Model model = new Model(
                            Optional.ofNullable(Identifier.of("fdmc", "block/piston/" + idTracker.getParent())),
                            Optional.of("piston/" + idTracker.getVariant()),
                            W_INDICATOR
                            );
                    model.upload(modelId, texture, blockStateModelGenerator.modelCollector);
                }

                //variant
                VariantSettings.Rotation[] rotation = PISTON_ROTATION.get(facing);

                BlockStateVariant variant = BlockStateVariant.create()
                        .put(VariantSettings.MODEL, modelId);

                if(rotation[0] != null) {
                    variant = variant.put(VariantSettings.X, rotation[0]);
                }
                if(rotation[1] != null) {
                    variant = variant.put(VariantSettings.Y, rotation[1]);
                }
                return variant;
            }
        ));


        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

    }


    private void createPistonHead(BlockStateModelGenerator blockStateModelGenerator){
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(Blocks.PISTON_HEAD);

        blockStateSupplier.coordinate(BlockStateVariantMap.create(Properties.FACING, Properties.SHORT, Properties.PISTON_TYPE).register(
                (facing, isShort, type) -> {
                    boolean isW = facing.getAxis() == Direction4Constants.Axis4Constants.W;
                    ParentIdTracker idTracker = new ParentIdTracker("piston_head");
                    TextureMap texture = new TextureMap();
                    boolean saveModel = isW;
                    if(isW) {
                        idTracker.append("_w");
                    }
                    if(isShort) {
                        if (!isW) {
                            idTracker.append("_short");
                        }
                        saveModel = false;
                    }
                    if(type == PistonType.STICKY) {
                        idTracker.append("_sticky");
                        texture.put(TextureKey.TEXTURE, Identifier.of("minecraft", "block/piston_top_sticky"));
                    }

                    if(facing == Direction4Constants.ANA){
                        idTracker.append("_ana");
                        texture.put(W_INDICATOR, Identifier.of("fdmc", "block/piston_ana"));
                    } else if(facing == Direction4Constants.KATA){
                        idTracker.append("_kata");
                        texture.put(W_INDICATOR, Identifier.of("fdmc", "block/piston_kata"));
                    }

                    //model
                    String namespace = "minecraft";
                    Identifier modelId = new Identifier(namespace, "block/" + idTracker.getVariant());
                    if(isW) {
                        namespace = "fdmc";
                        modelId = new Identifier(namespace, "block/piston/" + idTracker.getVariant());
                    }
                    if(saveModel) {
                        Identifier parentId = new Identifier(namespace, "block/piston/" + idTracker.getParent());
                        LOGGER.info("parent id: {} variant id: {}", parentId, modelId);
                        Model model = new Model(
                                Optional.ofNullable(Identifier.of("fdmc", "block/piston/" + idTracker.getParent())),
                                Optional.of("piston/" + idTracker.getVariant()),
                                W_INDICATOR
                        );
                        model.upload(modelId, texture, blockStateModelGenerator.modelCollector);
                    }

                    //variant
                    VariantSettings.Rotation[] rotation = PISTON_ROTATION.get(facing);

                    BlockStateVariant variant = BlockStateVariant.create()
                            .put(VariantSettings.MODEL, modelId);

                    if(rotation[0] != null) {
                        variant = variant.put(VariantSettings.X, rotation[0]);
                    }
                    if(rotation[1] != null) {
                        variant = variant.put(VariantSettings.Y, rotation[1]);
                    }
                    return variant;
                }
        ));


        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //TODO use a json parser to get the texture somehow
        BUTTONS.forEach((block, textureName) -> {
            createButton4(Identifier.of("minecraft", "block/" + textureName), block, blockStateModelGenerator);
        });
        createPiston(false, blockStateModelGenerator);
        createPiston(true, blockStateModelGenerator);
        createPistonHead(blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //none
    }

}

class ParentIdTracker {
    private String parent;
    private String variant;
    public ParentIdTracker(String variant) {
        this.variant = variant;
        this.parent = null;
    }

    public void pushNew(String path){
        this.parent = this.variant;
        this.variant = path;
    }

    public void append(String suffix){
        pushNew(variant + suffix);
    }

    public void prepend(String prefix){
        pushNew(prefix + variant);
    }

    public String getVariant() {
        return variant;
    }

    public String getParent() {
        return parent;
    }
}
