package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants.W_INDICATOR;
import static net.minecraft.util.math.AxisRotation.*;


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
        BUTTONS.put(
                Blocks.PALE_OAK_BUTTON,
                "pale_oak_planks"
        );
    }


    private static final HashMap<BlockFace, AxisRotation> BUTTON_FACE = Maps.newHashMap(ImmutableMap.of(
            BlockFace.FLOOR, R0   ,
            BlockFace.WALL , R90  ,
            BlockFace.CEILING , R180
    ));

    private static final HashMap<Direction, AxisRotation> BUTTON_ROTATION = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH , R0   ,
            Direction4Constants.EAST  , R90  ,
            Direction4Constants.SOUTH , R180 ,
            Direction4Constants.WEST  , AxisRotation.R270 ,
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
        LOGGER.info("Creating Button4 {} {}", texture, block);
        TextureMap textureMap = TextureMap.texture(texture);



        VariantsBlockModelDefinitionCreator blockStateSupplier = VariantsBlockModelDefinitionCreator.of(block).with(
                BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.BLOCK_FACE, Properties.POWERED)
                        .generate(
                                (facing, face, powered) -> {
                                    LOGGER.info("Properties facing {} face {} powered {}", facing, face, powered);
                                    boolean isW = facing.getAxis() == Direction4Constants.Axis4Constants.W;
                                    String variant;
                                    if(isW) {
                                        //wall and floor use same model
                                        String face_variant = (face == BlockFace.CEILING ? BlockFace.FLOOR : face).asString();
                                        //variant name
                                        variant = "_" + facing.name().toLowerCase() + "_" + face_variant + (powered ? "_pressed" : "");
                                    } else{
                                        variant = (powered ? "_pressed" : "");
                                    }


                                    RegistryKey<LootTable> key = block.getLootTableKey().orElseThrow();
                                    LOGGER.info("Loot table key: {}", key);
                                    Identifier modelId = Identifier.of(key.getValue().toString().replace("blocks/", "block/") + variant);
                                    Identifier parentId = Identifier.of("minecraft", "block/button" + variant);
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
                                        LOGGER.info("Uploaded parent id: {} variant id: {}", parentId, modelId);
                                        model.upload(block, textureMap, blockStateModelGenerator.modelCollector);
                                    } else {
                                        LOGGER.info("Skipped upload parent id: {} variant id: {}", parentId, modelId);
                                    }

//                                    return BlockStateVariant.create().put(
//                                            VariantSettings.X,
//                                            //don't rotate w walls ones only x z or floor/ceiling
//                                            isW && face == BlockFace.WALL? R0 : BUTTON_FACE.get(face)
//                                    ).put(
//                                            VariantSettings.Y,
//                                            BUTTON_ROTATION.get(facing)
//                                    ).put(
//                                            VariantSettings.MODEL,
//                                            modelId
//                                    );

                                    ModelVariant.ModelState modelState = new ModelVariant.ModelState(
                                            isW && face == BlockFace.WALL? R0 : BUTTON_FACE.get(face),
                                            BUTTON_ROTATION.get(facing),
                                            false
                                    );

                                    ModelVariant modelVariant = new ModelVariant(modelId, modelState);

                                    return new WeightedVariant(Pool.of(modelVariant));
                                }
                        )
        );


        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

    }
    private static final HashMap<Direction, AxisRotation[]> PISTON_ROTATION = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH , new AxisRotation[]{null , null},
            Direction4Constants.EAST  , new AxisRotation[]{null , R90 },
            Direction4Constants.SOUTH , new AxisRotation[]{null , R180},
            Direction4Constants.WEST  , new AxisRotation[]{null , R270},
            Direction4Constants.KATA  , new AxisRotation[]{null , null},
            Direction4Constants.ANA   , new AxisRotation[]{null , null},
            Direction4Constants.UP    , new AxisRotation[]{R270 , null},
            Direction4Constants.DOWN  , new AxisRotation[]{R90  , null}
    ));

    private void createPiston(boolean sticky, BlockStateModelGenerator blockStateModelGenerator){
        Block piston = sticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
        VariantsBlockModelDefinitionCreator blockStateSupplier =  VariantsBlockModelDefinitionCreator.of(piston).with(BlockStateVariantMap.models(Properties.FACING, Properties.EXTENDED).generate(
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
                Identifier modelId = Identifier.of(namespace, "block/" + idTracker.getVariant());
                if(isW) {
                    namespace = "fdmc";
                    modelId = Identifier.of(namespace, "block/piston/" + idTracker.getVariant());
                }
                if(saveModel) {
                    Identifier parentId = Identifier.of(namespace, "block/piston/" + idTracker.getParent());
                    LOGGER.info("parent id: {} variant id: {}", parentId, modelId);
                    Model model = new Model(
                            Optional.ofNullable(Identifier.of("fdmc", "block/piston/" + idTracker.getParent())),
                            Optional.of("piston/" + idTracker.getVariant()),
                            W_INDICATOR
                            );
                    model.upload(modelId, texture, blockStateModelGenerator.modelCollector);
                }

                //variant
                AxisRotation[] rotation = PISTON_ROTATION.get(facing);


                ModelVariant.ModelState modelState = new ModelVariant.ModelState(
                        rotation[0] == null ? R0 : rotation[0],
                        rotation[1] == null ? R0 : rotation[1],
                        false
                );

                ModelVariant modelVariant = new ModelVariant(modelId, modelState);

                return new WeightedVariant(Pool.of(modelVariant));

            }
        ));


        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

    }


    private void createPistonHead(BlockStateModelGenerator blockStateModelGenerator){
        VariantsBlockModelDefinitionCreator blockStateSupplier = VariantsBlockModelDefinitionCreator.of(Blocks.PISTON_HEAD).with(BlockStateVariantMap.models(Properties.FACING, Properties.SHORT, Properties.PISTON_TYPE).generate(
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
                    Identifier modelId = Identifier.of(namespace, "block/" + idTracker.getVariant());
                    if(isW) {
                        namespace = "fdmc";
                        modelId = Identifier.of(namespace, "block/piston/" + idTracker.getVariant());
                    }
                    if(saveModel) {
                        Identifier parentId = Identifier.of(namespace, "block/piston/" + idTracker.getParent());
                        LOGGER.info("parent id: {} variant id: {}", parentId, modelId);
                        Model model = new Model(
                                Optional.ofNullable(Identifier.of("fdmc", "block/piston/" + idTracker.getParent())),
                                Optional.of("piston/" + idTracker.getVariant()),
                                W_INDICATOR
                        );
                        model.upload(modelId, texture, blockStateModelGenerator.modelCollector);
                    }

                    //variant
                    AxisRotation[] rotation = PISTON_ROTATION.get(facing);

                    ModelVariant.ModelState modelState = new ModelVariant.ModelState(
                            rotation[0] == null ? R0 : rotation[0],
                            rotation[1] == null ? R0 : rotation[1],
                            false
                    );

                    ModelVariant modelVariant = new ModelVariant(modelId, modelState);

                    return new WeightedVariant(Pool.of(modelVariant));
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
