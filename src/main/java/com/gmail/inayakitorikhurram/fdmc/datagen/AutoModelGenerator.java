package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.state.property.EnumProperty4;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.*;
import net.minecraft.data.client.*;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.resource.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AutoModelGenerator extends FabricModelProvider {
    private final Gson GSON;
    private final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
    private final Map<Identifier, JsonModel> models = new HashMap<>();

    public AutoModelGenerator(FabricDataOutput output) {
        super(output);
        this.GSON = new GsonBuilder().registerTypeAdapter(JsonModel.class, new JsonModel.Deserializer(this::getModel)).registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer()).registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer()).registerTypeAdapter(Transformation.class, new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer()).registerTypeAdapter(ModelOverride.class, new ModelOverride.Deserializer()).create();
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

        BlockStateVariantMap4 blockStateVariantMap = new BlockStateVariantMap4(block);
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(block);

        blockStateSupplier.coordinate(blockStateVariantMap.register((propertiesMap, variants) -> {
            List<Property.Value<?>> property4Vals = propertiesMap.getValues().stream()
                    .map(Property.Value::property)
                    .filter(Property4.class::isInstance)
                    .map(AutoModelGenerator::createValueForAutoGen)
                    .collect(Collectors.toList());
            ExtendedPropertiesMap templatePropertiesMap = propertiesMap;
            for (Property.Value<?> value : property4Vals) {
                templatePropertiesMap = templatePropertiesMap.replaceValue(value);
            }
            VariantSettings.Rotation directionRotationY = property4Vals.stream()
                    .map(AutoModelGenerator::getRotation)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
            List<BlockStateVariant> templateVariants = variants.get(templatePropertiesMap);
            if (templateVariants == null || templateVariants.isEmpty()) {
                throw new RuntimeException();
            }
            List<BlockStateVariant> blockStateVariants = templateVariants.stream()
                    .map(templateVariant -> {
                        BlockStateVariant variant = BlockStateVariant.create();

                        Optional<VariantSettings.Rotation> templateRotationY =
                                Optional.of(minus(templateVariant.properties.containsKey(VariantSettings.Y)
                                                ? (VariantSettings.Rotation) templateVariant.properties.get(VariantSettings.Y).value
                                                : VariantSettings.Rotation.R0, directionRotationY))
                                        .filter(val -> val != VariantSettings.Rotation.R0);

                        Optional<VariantSettings.Rotation> templateRotationX = templateVariant.properties.containsKey(VariantSettings.X)
                                ? Optional.of((VariantSettings.Rotation) templateVariant.properties.get(VariantSettings.X).value)
                                        .filter(val -> val != VariantSettings.Rotation.R0)
                                : Optional.empty();

                        Optional<Boolean> templateUVLock = templateVariant.properties.containsKey(VariantSettings.UVLOCK) ?
                                Optional.of((boolean) templateVariant.properties.get(VariantSettings.UVLOCK).value) : Optional.empty();

                        Optional<Integer> templateWeight = templateVariant.properties.containsKey(VariantSettings.WEIGHT) ?
                                Optional.of((int) templateVariant.properties.get(VariantSettings.WEIGHT).value) : Optional.empty();

                        if (!templateVariant.properties.containsKey(VariantSettings.MODEL)) {
                            throw new RuntimeException();
                        }
                        Identifier templateModelID = (Identifier) templateVariant.properties.get(VariantSettings.MODEL).value;
                        Optional<JsonModel> templateModel = this.getModel(templateModelID);
                        if (templateModel.isEmpty()) {
                            throw new RuntimeException();
                        }
                        Identifier variantModelID = Identifier.of(templateModelID.getNamespace(), templateModelID.getPath() + "_w_autogen");
                        Optional<JsonModel> variantModel = this.getModel(variantModelID);
                        if (variantModel.isEmpty()) {
                            boolean transformationSuccess = templateModel.map(model -> model.performWTransformation(templateModelID, "_w_autogen", templateRotationX.orElse(VariantSettings.Rotation.R0), templateRotationY.orElse(VariantSettings.Rotation.R0), this::getModel, this::registerModel, blockStateModelGenerator.modelCollector))
                                    .orElse(false);
                            if (transformationSuccess) {
                                variant.put(VariantSettings.MODEL, variantModelID);
                            } else {
                                variant.put(VariantSettings.MODEL, templateModelID);
                            }
                        } else {
                            variant.put(VariantSettings.MODEL, variantModelID);
                        }

                        templateRotationX.ifPresent(rotationX -> variant.put(VariantSettings.X, rotationX));
                        templateRotationY.ifPresent(rotationX -> variant.put(VariantSettings.Y, rotationX));
                        templateUVLock.ifPresent(uvlock -> variant.put(VariantSettings.UVLOCK, uvlock));
                        templateWeight.ifPresent(weight -> variant.put(VariantSettings.WEIGHT, weight));
                        return variant;
                    }).collect(Collectors.toList());


            return blockStateVariants;
        }));

        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);
    }

    private static Property.Value<?> createValueForAutoGen(Property<?> property) {
        if (property instanceof DirectionProperty directionProperty) {
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
        } else if (property instanceof EnumProperty4) {
            List<?> values = List.copyOf(property.getValues());
            if (values.get(0) instanceof Direction.Axis) {
                if (values.contains(Direction.Axis.Z)) {
                    return constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, Direction.Axis.Z);
                } else if (values.contains(Direction.Axis.X)) {
                    return constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, Direction.Axis.X);
                }
            }
        }
        throw new RuntimeException();
    }

    private static VariantSettings.Rotation getRotation(Property.Value<?> value) {
        Object val = value.value();
        if (val instanceof Direction direction) {
            return switch (direction) {
                case NORTH -> VariantSettings.Rotation.R0;
                case EAST -> VariantSettings.Rotation.R90;
                case SOUTH -> VariantSettings.Rotation.R180;
                case WEST -> VariantSettings.Rotation.R270;
                default -> null;
            };
        } else if (val instanceof Direction4.Axis4 axis) {
            return switch (axis.asEnum()) {
                case Z -> VariantSettings.Rotation.R0;
                case X -> VariantSettings.Rotation.R90;
                default -> null;
            };
        }
        return null;
    }

    private static VariantSettings.Rotation minus(VariantSettings.Rotation rotation1, VariantSettings.Rotation rotation2) {
        return switch (rotation2) {
            case R0 -> rotation1;
            case R90 -> switch (rotation1) {
                case R0 -> VariantSettings.Rotation.R270;
                case R90 -> VariantSettings.Rotation.R0;
                case R180 -> VariantSettings.Rotation.R90;
                case R270 -> VariantSettings.Rotation.R180;
            };
            case R180 -> switch (rotation1) {
                case R0 -> VariantSettings.Rotation.R180;
                case R90 -> VariantSettings.Rotation.R270;
                case R180 -> VariantSettings.Rotation.R0;
                case R270 -> VariantSettings.Rotation.R90;
            };
            case R270 -> switch (rotation1) {
                case R0 -> VariantSettings.Rotation.R90;
                case R90 -> VariantSettings.Rotation.R180;
                case R180 -> VariantSettings.Rotation.R270;
                case R270 -> VariantSettings.Rotation.R0;
            };
        };
    }

    private Optional<JsonModel> getModel(Identifier modelId) {
        if (models.containsKey(modelId)) {
            return Optional.of(models.get(modelId));
        }
        try {
            Optional<Resource> resource = resourceManager.getResource(ModelLoader.MODELS_FINDER.toResourcePath(modelId));
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
    private static <T extends Comparable<T>> BiConsumer<Property<?>, Comparable<?>> constrainPropertyBiConsumer(BiConsumer<Property<T>, T> biConsumer) {
        return (property, value) -> biConsumer.accept((Property<T>) property, (T) value);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>, R> BiFunction<Property<?>, Comparable<?>, R> constrainPropertyBiFunction(BiFunction<Property<T>, T, R> biFunction) {
        return (property, value) -> biFunction.apply((Property<T>) property, (T) value);
    }

    private static class BlockStateVariantMap4 extends BlockStateVariantMap {
        private final Block block;
        private final Map<ExtendedPropertiesMap, List<BlockStateVariant>> extendedVariants = Maps.newHashMap();

        BlockStateVariantMap4(Block block) {
            this.block = block;
            try {
                Reader reader = MinecraftClient.getInstance().getResourceManager()
                        .getResource(ModelLoader.BLOCK_STATES_FINDER.toResourcePath(block.getRegistryEntry().registryKey().getValue()))
                        .orElseThrow()
                        .getReader();
                JsonObject jsonObject = JsonHelper.deserialize(reader);
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
                    List<BlockStateVariant> variants;
                    if (jsonElement.isJsonArray()) {
                        variants = jsonElement.getAsJsonArray().asList().stream()
                                .map(element -> BlockStateVariantDeserializer.GSON.fromJson(element, BlockStateVariant.class))
                                .collect(Collectors.toList());
                    } else {
                        variants = List.of(BlockStateVariantDeserializer.GSON.fromJson(jsonElement, BlockStateVariant.class));
                    }
                    this.register(propertiesMap, variants);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        private <T extends Comparable<T>> Property.Value<T> getValue(Property<T> property, String value) {
            return property.createValue(property.parse(value).orElseThrow());
        }

        @Override
        protected void register(PropertiesMap condition, List<BlockStateVariant> possibleVariants) {
            super.register(condition, possibleVariants);
            this.extendedVariants.put(ExtendedPropertiesMap.of(condition), possibleVariants);
        }

        public List<Property<?>> getProperties() {
            return List.copyOf(block.getStateManager().getProperties());
        }

        public boolean hasVariant(ExtendedPropertiesMap propertiesMap) {
            return this.extendedVariants.containsKey(propertiesMap);
        }

        public BlockStateVariantMap register(BiFunction<ExtendedPropertiesMap, Map<ExtendedPropertiesMap, List<BlockStateVariant>>, List<BlockStateVariant>> variantFactory) {
            this.block.getStateManager().getProperties().stream()
                    .map(property -> Property4.getValues(property).stream()
                            .map(value -> constrainPropertyBiFunction((prop, val) -> prop.createValue(val)).apply(property, value))
                            .map(List::of)
                            .collect(Collectors.toList()))
                    .reduce((list1, list2) -> list2.stream()
                            .flatMap(propertyVariations2 -> list1.stream()
                                    .map(ArrayList::new)
                                    .peek(propertyVariations1 -> propertyVariations1.addAll(propertyVariations2)))
                            .collect(Collectors.toList()))
                    .stream()
                    .flatMap(Collection::stream)
                    .map(list -> list.toArray(Property.Value<?>[]::new))
                    .map(ExtendedPropertiesMap::withValues)
                    .filter(Predicate.not(this::hasVariant))
                    .forEach(propertiesMap -> this.register(propertiesMap, variantFactory.apply(propertiesMap, this.extendedVariants)));

            return this;
        }
    }

    public static class BlockStateVariantDeserializer implements JsonDeserializer<BlockStateVariant> {
        public static Gson GSON = new GsonBuilder().registerTypeAdapter(BlockStateVariant.class, new BlockStateVariantDeserializer()).create();

        private static final Map<String, BiConsumer<BlockStateVariant, JsonElement>> VARIANT_SETTINGS = Map.of(
                VariantSettings.X.toString()     , BlockStateVariantDeserializer::applyVariantSettingX,
                VariantSettings.Y.toString()     , BlockStateVariantDeserializer::applyVariantSettingY,
                VariantSettings.MODEL.toString() , BlockStateVariantDeserializer::applyVariantSettingModel,
                VariantSettings.UVLOCK.toString(), BlockStateVariantDeserializer::applyVariantSettingUVLock,
                VariantSettings.WEIGHT.toString(), BlockStateVariantDeserializer::applyVariantSettingWeight);

        private static void applyVariantSettingX(BlockStateVariant variant, JsonElement jsonElement) {
            variant.put(VariantSettings.X, VariantSettings.Rotation.valueOf("R" + jsonElement.getAsString()));
        }

        private static void applyVariantSettingY(BlockStateVariant variant, JsonElement jsonElement) {
            variant.put(VariantSettings.Y, VariantSettings.Rotation.valueOf("R" + jsonElement.getAsString()));
        }

        private static void applyVariantSettingModel(BlockStateVariant variant, JsonElement jsonElement) {
            variant.put(VariantSettings.MODEL, new Identifier(jsonElement.getAsString()));
        }

        private static void applyVariantSettingUVLock(BlockStateVariant variant, JsonElement jsonElement) {
            variant.put(VariantSettings.UVLOCK, jsonElement.getAsBoolean());
        }

        private static void applyVariantSettingWeight(BlockStateVariant variant, JsonElement jsonElement) {
            variant.put(VariantSettings.WEIGHT, jsonElement.getAsInt());
        }

        @Override
        public BlockStateVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BlockStateVariant variant = BlockStateVariant.create();
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                VARIANT_SETTINGS.getOrDefault(entry.getKey(), (a, b) -> {throw new RuntimeException();}).accept(variant, entry.getValue());
            }
            return variant;
        }
    }
}
