package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.state.property.EnumProperty4;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.data.*;
import net.minecraft.resource.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.util.math.AxisRotation.*;

public class AutoModelGenerator extends FabricModelProvider {
    private final Gson GSON;
    private final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
    private final Map<Identifier, JsonModel> models = new HashMap<>();


    public AutoModelGenerator(FabricDataOutput output) {
        super(output);
        this.GSON = new GsonBuilder()
                .registerTypeAdapter(JsonModel.class, new JsonModel.Deserializer(this::getModel))
                .registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
                .registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
//                .registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
                .registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
                .registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
//                .registerTypeAdapter(ModelOverride.class, new ModelOverride.Deserializer())
                .create();
    }

    @Override
    public String getName() {
        return "AutoModelGenerator";
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        List.of(
                Blocks.END_ROD,
                Blocks.NETHER_PORTAL
        ).forEach(block -> this.generateBlockStateModels(block, blockStateModelGenerator));
    }

    public void generateBlockStateModels(Block block, BlockStateModelGenerator blockStateModelGenerator) {
        StateManager<Block, BlockState> stateManager = block.getStateManager();

        List<Property<?>> properties = List.copyOf(stateManager.getProperties());
        if (properties.stream().noneMatch(Property4.class::isInstance)) { // TODO: better logging
            throw new UnsupportedOperationException("Cannot generate models for blocks without any Property4!");
        }

        BlockStateVariantMap4<ModelVariant, WeightedVariant> blockStateVariantMap =
                new BlockStateVariantMap4<>(block, jsonElement -> ModelVariantDeserializer.GSON.fromJson(jsonElement, ModelVariant.class), modelVariant -> new WeightedVariant(Pool.of(modelVariant)));
        VariantsBlockModelDefinitionCreator blockStateSupplier = VariantsBlockModelDefinitionCreator.of(block)
        .with(blockStateVariantMap.generate((propertiesMap, variants) -> {
            List<Property.Value<?>> property4Vals = propertiesMap.getValues().stream()
                    .map(Property.Value::property)
                    .filter(Property4.class::isInstance)
                    .map(AutoModelGenerator::createValueForAutoGen)
                    .collect(Collectors.toList());
            ExtendedPropertiesMap templatePropertiesMap = propertiesMap;
            for (Property.Value<?> value : property4Vals) {
                templatePropertiesMap = templatePropertiesMap.replaceValue(value);
            }
            AxisRotation directionRotationY = property4Vals.stream()
                    .map(AutoModelGenerator::getRotation)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
            List<ModelVariant> templateVariants = variants.get(templatePropertiesMap);
            if (templateVariants == null) {
                throw new RuntimeException();
            }
            List<ModelVariant> modelVariants = templateVariants.stream()
                    .map(templateVariant -> {

                        AxisRotation templateRotationX = templateVariant.modelState().x();
                        AxisRotation templateRotationY = templateVariant.modelState().y();


                        boolean templateUVLock = templateVariant.modelState().uvLock();


                        Identifier templateModelID = templateVariant.modelId();
                        Optional<JsonModel> templateModel = this.getModel(templateModelID);
                        if (templateModel.isEmpty()) {
                            throw new RuntimeException();
                        }
                        Identifier variantModelID = Identifier.of(templateModelID.getNamespace(), templateModelID.getPath() + "_w_autogen");
                        Optional<JsonModel> variantModel = this.getModel(variantModelID);
                        Identifier modelIDToUse = templateModelID;
                        if (variantModel.isEmpty()) {
                            boolean transformationSuccess = templateModel.map(model ->
                                            model.performWTransformation(templateModelID, "_w_autogen",
                                                    templateRotationX,
                                                    templateRotationY,
                                                    this::getModel, this::registerModel,
                                                    blockStateModelGenerator.modelCollector))
                                    .orElse(false);
                            if (transformationSuccess) {
                                modelIDToUse = variantModelID;
                            }
                        } else {
                            modelIDToUse = variantModelID;
                        }

                        ModelVariant.ModelState state = new ModelVariant.ModelState(templateRotationX, templateRotationY, templateUVLock);

                        ModelVariant variant = new ModelVariant(modelIDToUse, state);

                        return variant;
                    }).collect(Collectors.toList());


            return modelVariants;
        }));

        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);
    }

    private static Property.Value<?> createValueForAutoGen(Property<?> property) {
        FDMCConstants.LOGGER.info("\ncreateValueForAutoGen: {}", property);
        if (property instanceof EnumProperty4) {
            FDMCConstants.LOGGER.info("Property is an EnumProperty4 {}", property);
            List<?> values = List.copyOf(property.getValues());
            if (values.get(0) instanceof Direction.Axis) {
                if (values.contains(Direction.Axis.Z)) {
                    return constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, Direction.Axis.Z);
                } else if (values.contains(Direction.Axis.X)) {
                    return constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, Direction.Axis.X);
                }
            }
        }
        if (property.getType() == Direction.class) {
            FDMCConstants.LOGGER.info("Property is a direction {}", property);
            Property<Direction> directionProperty = (Property<Direction>) property;
            Set<Direction> values = Set.copyOf(directionProperty.getValues());
            if (values.contains(Direction.NORTH)) {
                return directionProperty.createValue(Direction.NORTH);
            } else if (values.contains(Direction.SOUTH)) {
                return directionProperty.createValue(Direction.SOUTH);
            } else if (values.contains(Direction.WEST)) {
                return directionProperty.createValue(Direction.WEST);
            } else if (values.contains(Direction.EAST)) {
                return directionProperty.createValue(Direction.EAST);
            }
        }
//        if(property == Properties.WATERLOGGED || property == Properties.POWERED){
//            BooleanProperty typedProperty = (BooleanProperty) property;
//            List<Boolean> values =  typedProperty.getValues();
//                if(values.contains(false)){
//                    return typedProperty.createValue(false);
//                } else if(values.contains(true)) {
//                    return typedProperty.createValue(true);
//                }
//                throw new IllegalArgumentException();
//        }

        throw new RuntimeException();
    }

    private static AxisRotation getRotation(Property.Value<?> value) {
        Object val = value.value();
        if (val instanceof Direction direction) {
            return switch (direction) {
                case NORTH -> R0;
                case EAST -> R90;
                case SOUTH -> R180;
                case WEST -> R270;
                default -> null;
            };
        } else if (val instanceof Direction4.Axis4 axis) {
            return switch (axis.asEnum()) {
                case Z -> R0;
                case X -> R90;
                default -> null;
            };
        }
        return null;
    }

    private static AxisRotation minus(AxisRotation rotation1, AxisRotation rotation2) {
        return switch (rotation2) {
            case R0 -> rotation1;
            case R90 -> switch (rotation1) {
                case R0 -> R270;
                case R90 -> R0;
                case R180 -> R90;
                case R270 -> R180;
            };
            case R180 -> switch (rotation1) {
                case R0 -> R180;
                case R90 -> R270;
                case R180 -> R0;
                case R270 -> R90;
            };
            case R270 -> switch (rotation1) {
                case R0 -> R90;
                case R90 -> R180;
                case R180 -> R270;
                case R270 -> R0;
            };
        };
    }

    private Optional<JsonModel> getModel(Identifier modelId) {
        if (models.containsKey(modelId)) {
            return Optional.of(models.get(modelId));
        }
        try {
            Optional<Resource> resource = resourceManager.getResource(BakedModelManager.MODELS_FINDER.toResourcePath(modelId));
            if (resource.isEmpty()) {
                return Optional.empty();
            }
            JsonModel model = JsonHelper.deserialize(GSON, resource.get().getReader(), JsonModel.class);
            models.put(modelId, model);
            return Optional.of(model);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void registerModel(Identifier modelId, JsonModel model) {
        if (models.put(modelId, model) != null) {
            throw new RuntimeException();
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>, R> BiFunction<Property<?>, Comparable<?>, R> constrainPropertyBiFunction(BiFunction<Property<T>, T, R> biFunction) {
        return (property, value) -> biFunction.apply((Property<T>) property, (T) value);
    }


    // Needs two variables because of the distinction between the
    private static class BlockStateVariantMap4<V,W> extends BlockStateVariantMap<W> {
        private final Block block;
        private final Map<ExtendedPropertiesMap, List<V>> extendedVariants = Maps.newHashMap();
        private final Function<V, W> variantMap;

        BlockStateVariantMap4(Block block, Function<JsonElement, V> deserialize, Function<V, W> variantMap) {
            this.block = block;
            this.variantMap = variantMap;
            try {
                Identifier blockKey = block.getLootTableKey().orElseThrow().getValue();
                // TODO fix up
                Identifier resourcePathInitial = blockKey;
                Identifier modifiedResourcePath = Identifier.of(resourcePathInitial.toString().replace("blocks/","blockstates/") + ".json");
                FDMCConstants.LOGGER.info("block key: {} resource path: {}", blockKey, modifiedResourcePath);
                Reader reader = MinecraftClient.getInstance().getResourceManager()
                        .getResource(modifiedResourcePath)
                        .orElseThrow()
                        .getReader();
                JsonObject jsonObject = JsonHelper.deserialize(reader);
                FDMCConstants.LOGGER.info("{}", jsonObject);
                if (!jsonObject.has("variants")) {
                    throw new RuntimeException();
                }


                StateManager<Block, BlockState> stateManager = block.getStateManager();
                BlockState defaultState = stateManager.getDefaultState();
                ExtendedPropertiesMap defaultPropertiesMap = ExtendedPropertiesMap.empty();
                for (Property<?> property : block.getStateManager().getProperties()) {
                    defaultPropertiesMap = defaultPropertiesMap.withValue(property.createValue(defaultState));
                }

                for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("variants").entrySet()) {
                    ExtendedPropertiesMap propertiesMap = defaultPropertiesMap;
                    if (!entry.getKey().equals("")) {
                        for (String propertyValuePair : entry.getKey().split(",")) {
                            String[] split = propertyValuePair.split("=");
                            Property<?> property = Objects.requireNonNull(stateManager.getProperty(split[0]));
                            propertiesMap = propertiesMap.replaceValue(getValue(property, split[1]));
                        }
                    }
                    JsonElement jsonElement = entry.getValue();
                    List <V> variants;
                    if (jsonElement.isJsonArray()) {
                        variants = jsonElement.getAsJsonArray().asList().stream()
                                .map(deserialize)
                                .collect(Collectors.toList());
                    } else {
                        variants = List.of(deserialize.apply(jsonElement));
                    }
                    this.registerAll(propertiesMap, variants);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        private <T extends Comparable<T>> Property.Value<T> getValue(Property<T> property, String value) {
            return property.createValue(property.parse(value).orElseThrow());
        }

        protected void registerAll(PropertiesMap condition, List<V> possibleVariants) {
            for (V variant : possibleVariants) {
                super.register(condition, variantMap.apply(variant));
            }
            this.extendedVariants.put(ExtendedPropertiesMap.of(condition), possibleVariants);
        }

        public List<Property<?>> getProperties() {
            return List.copyOf(block.getStateManager().getProperties());
        }

        public boolean hasVariant(ExtendedPropertiesMap propertiesMap) {
            return this.extendedVariants.containsKey(propertiesMap);
        }


        public BlockStateVariantMap<W> generate(BiFunction<ExtendedPropertiesMap, Map<ExtendedPropertiesMap, List<V>>, List<V>> variantFactory) {
            this.block.getStateManager().getProperties().stream()
                    .map(property -> Property4.getValues(property).stream()
                            .map(value -> constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, value))
                            .map(List::of)
                            .collect(Collectors.toList()))
                    .peek(lists -> FDMCConstants.LOGGER.info("point 1: {}", lists))
                    .reduce((list1, list2) -> list2.stream()
                            .flatMap(propertyVariations2 -> list1.stream()
                                    .map(ArrayList::new)
                                    .peek(propertyVariations1 -> propertyVariations1.addAll(propertyVariations2)))
                            .collect(Collectors.toList()))
                    .stream()
                    .peek(lists -> FDMCConstants.LOGGER.info("point 2: {}", lists))
                    .flatMap(Collection::stream)
                    .map(list -> list.toArray(Property.Value<?>[]::new))
                    .peek(lists -> FDMCConstants.LOGGER.info("point 3: {}", (Object) lists))
                    .map(ExtendedPropertiesMap::withValues)
                    .peek(lists -> FDMCConstants.LOGGER.info("point 4: {}", lists))
                    .filter(Predicate.not(this::hasVariant))
                    .peek(lists -> FDMCConstants.LOGGER.info("point 5: {}", lists))
                    .forEach(propertiesMap -> this.registerAll(propertiesMap, variantFactory.apply(propertiesMap, this.extendedVariants)));

            return this;
        }
    }

    public static class ModelVariantDeserializer implements JsonDeserializer<ModelVariant> {
        public static Gson GSON = new GsonBuilder().registerTypeAdapter(ModelVariant.class, new ModelVariantDeserializer()).create();

        private static final Map<String, BiFunction<ModelVariant, JsonElement, ModelVariant>> VARIANT_SETTINGS = Map.of(
                "x"     , ModelVariantDeserializer::applyVariantSettingX,
                "y"     , ModelVariantDeserializer::applyVariantSettingY,
                "model" , ModelVariantDeserializer::applyVariantSettingModel,
                "uvlock", ModelVariantDeserializer::applyVariantSettingUVLock);

        private static ModelVariant applyVariantSettingX(ModelVariant variant, JsonElement jsonElement) {
            return variant.setState(variant.modelState().setRotationX(valueOf("R" + jsonElement.getAsString())));
        }

        private static ModelVariant applyVariantSettingY(ModelVariant variant, JsonElement jsonElement) {
            return variant.setState(variant.modelState().setRotationY(valueOf("R" + jsonElement.getAsString())));
        }

        private static ModelVariant applyVariantSettingModel(ModelVariant variant, JsonElement jsonElement) {
            return variant.withModel(Identifier.of(jsonElement.getAsString()));
        }

        private static ModelVariant applyVariantSettingUVLock(ModelVariant variant, JsonElement jsonElement) {
            return variant.setState(variant.modelState().setUVLock(jsonElement.getAsBoolean()));
        }

        private static ModelVariant applyVariantSettingWeight(ModelVariant variant, JsonElement jsonElement) {
            //TODO
            throw new NotImplementedException();
        }

        @Override
        public ModelVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            FDMCConstants.LOGGER.info("Deserializing: {}:\n{}\n", jsonElement, type);
            ModelVariant variant = new ModelVariant(Identifier.of("none"));
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                variant = VARIANT_SETTINGS.getOrDefault(entry.getKey(), (a, b) -> {throw new RuntimeException();}).apply(variant, entry.getValue());
            }
            return variant;
        }
    }
}
