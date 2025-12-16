package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonModel extends Model {
    private final Map<String, TextureKey> textureKeyMap = new HashMap<>();
    private final List<ModelElement> elements;
    private final TextureMap textures;
    private final Optional<Boolean> ambientOcclusion;
    // can't be used in block models
    //private final List<ModelOverride> modelOverrides;
    private final ModelTransformation modelTransformation;
    // can't be used in block models
    //private final Optional<JsonUnbakedModel.GuiLight> guiLight;

    public JsonModel(Optional<Identifier> parent,
                     Optional<String> variant,
                     List<ModelElement> elements,
                     TextureMap textures,
                     Optional<Boolean> ambientOcclusion,
                     // can't be used in block models
                     //List<ModelOverride> modelOverrides,
                     ModelTransformation modelTransformation,
                     // can't be used in block models
                     //Optional<JsonUnbakedModel.GuiLight> guiLight,
                     TextureKey... requiredTextureKeys) {

        super(parent, variant, requiredTextureKeys);

        this.elements = elements;
        this.textures = textures;
        this.ambientOcclusion = ambientOcclusion;
        // can't be used in block models
        //this.modelOverrides = modelOverrides;
        this.modelTransformation = modelTransformation;
        // can't be used in block models
        //this.guiLight = guiLight;
    }

    public JsonModel copyWithParentAndElements(Optional<Identifier> parent, List<ModelElement> elements, Optional<String> variant) {
        return new JsonModel(
                parent,
                variant.isPresent() ? variant : this.variant,
                elements,
                this.textures,
                this.ambientOcclusion,
                this.modelTransformation,
                this.requiredTextures.toArray(TextureKey[]::new));
    }

    protected Map<String, TextureKey> copyTextureKeyMap() {
        return new HashMap<>(textureKeyMap);
    }

    public Optional<Identifier> getParent() {
        return this.parent;
    }

    public Set<TextureKey> getRequiredTextures() {
        return Set.copyOf(this.requiredTextures);
    }

    public List<ModelElement> getElements() {
        return this.elements;
    }

    public TextureMap getTextureMap() {
        return this.textures;
    }

    public Optional<Boolean> getAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    /* can't be used in block models
    public List<ModelOverride> getModelOverrides() {
        return List.copyOf(this.modelOverrides);
    }
     */

    public ModelTransformation getModelTransformation() {
        return this.modelTransformation;
    }

    /* can't be used in block models
    public Optional<JsonUnbakedModel.GuiLight> getGuiLight() {
        return this.guiLight;
    }
     */

    public boolean performWTransformation(Identifier modelID, String variant, AxisRotation rotationX, AxisRotation rotationY, Function<Identifier, Optional<JsonModel>> modelProvider, BiConsumer<Identifier, JsonModel> registerModel, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        rotationY = switch (rotationX) {
            case R0, R90 -> rotationY;
            case R180, R270 -> switch (rotationY) {
                case R90 -> AxisRotation.R270;
                case R270 -> AxisRotation.R90;
                default -> rotationY;
            };
        };
        rotationX = switch (rotationY) {
            case R0, R90 -> rotationX;
            case R180 -> AxisRotation.R0;
            case R270 -> AxisRotation.R90;
        };
        AtomicBoolean transformationSuccess = new AtomicBoolean(false);
        boolean parentTransformation = false;
        if (this.parent.isPresent()) {
            AxisRotation lambdaRotationX = rotationX;
            AxisRotation lambdaRotationY = rotationY;
            parentTransformation = this.parent.flatMap(modelProvider).map(model -> model.performWTransformation(parent.get(), variant, lambdaRotationX, lambdaRotationY, modelProvider, registerModel, modelCollector)).orElseThrow();
        }

        int axis;
        int orthogonalAxis;
        int verticalAxis;

        switch (rotationX) {
            case R0 -> {
                verticalAxis = 1;
                switch (rotationY) {
                    case R0, R180 -> {
                        axis = 2;
                        orthogonalAxis = 0;
                    }
                    case R90, R270 -> {
                        axis = 0;
                        orthogonalAxis = 2;
                    }
                    default -> throw new RuntimeException();
                }
            }
            case R90 -> {
                axis = 1;
                switch (rotationY) {
                    case R0, R180 -> {
                        orthogonalAxis = 0;
                        verticalAxis = 2;
                    }
                    case R90, R270 -> {
                        orthogonalAxis = 2;
                        verticalAxis = 0;
                    }
                    default -> throw new RuntimeException();
                }
            }
            default -> throw new RuntimeException();
        }
        List<ModelElement> transformedElements = this.elements.stream().map(element -> {
            // TODO: make this behave way better. Currently questionable at best.
            boolean transform = false;
            Vector3fc from;
            Vector3fc to;
            if (element.from().get(orthogonalAxis) != element.from().get(axis)) {
                transform = true;
                Vector3f vec = new Vector3f(0, 0, 0);
                vec.setComponent(verticalAxis, element.from().get(verticalAxis));
                vec.setComponent(orthogonalAxis, element.from().get(orthogonalAxis));
                vec.setComponent(axis, element.from().get(orthogonalAxis));
                from = vec;
            } else {
                from = element.from();
            }
            if (element.to().get(orthogonalAxis) != element.to().get(axis)) {
                transform = true;
                Vector3f vec = new Vector3f(0, 0, 0);
                vec.setComponent(verticalAxis, element.to().get(verticalAxis));
                vec.setComponent(orthogonalAxis, element.to().get(orthogonalAxis));
                vec.setComponent(axis, element.to().get(orthogonalAxis));
                to = vec;
            } else {
                to = element.to();
            }
            if (!transform) {
                return element;
            }
            transformationSuccess.set(true);
            Map<Direction, ModelElementFace> faces = new HashMap<>();
            element.faces().forEach((direction, face) -> {
                boolean fixAttributes = false;
                if (face.cullFace() != null) {
                    fixAttributes = true;
                }
                int uvX;
                int uvY;
                switch (direction.getAxis()) {
                    case X -> {
                        uvX = 2;
                        uvY = 1;
                    }
                    case Y -> {
                        uvX = 0;
                        uvY = 2;
                    }
                    case Z -> {
                        uvX = 0;
                        uvY = 1;
                    }
                    default -> throw new RuntimeException();
                }
                ModelElementFace.UV uvs = face.uvs();
                float scaleX = (to.get(uvX) - from.get(uvX)) / (element.to().get(uvX) - element.from().get(uvX));
                float scaleY = (to.get(uvY) - from.get(uvY)) / (element.to().get(uvY) - element.from().get(uvY));
                if (scaleX != 1 || scaleY != 1) {
                    uvs = rescaleUVs(uvs, scaleX, scaleY);
                    fixAttributes = true;
                }

                if (fixAttributes) {
                    faces.put(direction, new ModelElementFace(null, face.tintIndex(), face.textureId(), uvs, face.rotation()));
                } else {
                    faces.put(direction, face);
                }
            });

            Direction dir1;
            Direction dir2;
            switch (axis) {
                case 0 -> {
                    dir1 = Direction.WEST;
                    dir2 = Direction.EAST;
                }
                case 1 -> {
                    dir1 = Direction.DOWN;
                    dir2 = Direction.UP;
                }
                case 2 -> {
                    dir1 = Direction.NORTH;
                    dir2 = Direction.SOUTH;
                }
                default -> throw new RuntimeException();
            }

            Direction dir3;
            Direction dir4;
            switch (orthogonalAxis) {
                case 0 -> {
                    dir3 = Direction.WEST;
                    dir4 = Direction.EAST;
                }
                case 1 -> {
                    dir3 = Direction.DOWN;
                    dir4 = Direction.UP;
                }
                case 2 -> {
                    dir3 = Direction.NORTH;
                    dir4 = Direction.SOUTH;
                }
                default -> throw new RuntimeException();
            }

            ModelElementFace face = Stream.of(dir1, dir2, dir3, dir4).filter(faces::containsKey).findFirst().map(faces::get).orElseThrow();
            faces.putIfAbsent(dir1, face);
            faces.putIfAbsent(dir2, face);
            faces.putIfAbsent(dir3, face);
            faces.putIfAbsent(dir4, face);

            return new ModelElement(from, to, faces, element.rotation(), element.shade(), element.lightEmission());
        }).collect(Collectors.toList());

        if (transformationSuccess.get() || parentTransformation) {
            JsonModel transformedModel = this.copyWithParentAndElements(parentTransformation ? Optional.of(Identifier.of(parent.get().getNamespace(), parent.get().getPath() + variant)) : parent,
                    transformationSuccess.get() ? transformedElements : this.elements,
                    this.variant);
            Identifier transformedModelID = Identifier.of(modelID.getNamespace(), modelID.getPath() + variant);
            registerModel.accept(transformedModelID, transformedModel);
            transformedModel.upload(transformedModelID, this.textures, modelCollector);
        }

        return transformationSuccess.get();
    }

    private static ModelElementFace.UV rescaleUVs(ModelElementFace.UV uvs, float scaleX, float scaleY) {
        float minU = MathHelper.clamp((uvs.minU() - 8) * scaleX + 8, 0, 16);
        float minV = MathHelper.clamp((uvs.minV() - 8) * scaleY + 8, 0, 16);
        float maxU = MathHelper.clamp((uvs.maxU() - 8) * scaleX + 8, 0, 16);
        float maxV = MathHelper.clamp((uvs.maxV() - 8) * scaleY + 8, 0, 16);
        return new ModelElementFace.UV(minU, minV, maxU, maxV);
    }

    public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        Map<TextureKey, Identifier> map = this.createTextureMap(textures);
        modelCollector.accept(id, () -> {
            JsonObject jsonObject = new JsonObject();
            this.parent.ifPresent(parentId -> jsonObject.addProperty("parent", parentId.toString()));
            this.ambientOcclusion.ifPresent(value -> jsonObject.addProperty("ambientocclusion", value));
            if (this.modelTransformation != ModelTransformation.NONE) {
                jsonObject.add("display", writeDisplay(this.modelTransformation));
            }
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((textureKey, textureId) -> jsonObject2.addProperty(textureKey.getName(), textureId.toString()));
                jsonObject.add("textures", jsonObject2);
            }
            if (!this.elements.isEmpty()) {
                jsonObject.add("elements", writeElements(this.elements));
            }
            return jsonObject;
        });
        return id;
    }

    private static JsonObject writeDisplay(ModelTransformation display) {
        JsonObject jsonObject = new JsonObject();
        if (display.thirdPersonRightHand() != Transformation.IDENTITY) {
            jsonObject.add("thirdperson_righthand", writeTransformation(display.thirdPersonRightHand()));
        }
        if (display.thirdPersonLeftHand() != Transformation.IDENTITY && display.thirdPersonLeftHand() != display.thirdPersonRightHand()) {
            jsonObject.add("thirdperson_lefthand", writeTransformation(display.thirdPersonLeftHand()));
        }

        if (display.firstPersonRightHand() != Transformation.IDENTITY) {
            jsonObject.add("firstperson_righthand", writeTransformation(display.firstPersonRightHand()));
        }
        if (display.firstPersonLeftHand() != Transformation.IDENTITY && display.firstPersonLeftHand() != display.firstPersonRightHand()) {
            jsonObject.add("firstperson_lefthand", writeTransformation(display.firstPersonLeftHand()));
        }

        if (display.head() != Transformation.IDENTITY) {
            jsonObject.add("head", writeTransformation(display.head()));
        }
        if (display.gui() != Transformation.IDENTITY) {
            jsonObject.add("gui", writeTransformation(display.gui()));
        }
        if (display.ground() != Transformation.IDENTITY) {
            jsonObject.add("ground", writeTransformation(display.ground()));
        }
        if (display.fixed() != Transformation.IDENTITY) {
            jsonObject.add("fixed", writeTransformation(display.fixed()));
        }
        return jsonObject;
    }

    private static JsonObject writeTransformation(Transformation transformation) {
        JsonObject jsonObject = new JsonObject();
        if (!transformation.rotation().equals(Transformation.Deserializer.DEFAULT_ROTATION)) {
            jsonObject.add("rotation", writeVector3fc(transformation.rotation()));
        }
        if (!transformation.translation().equals(Transformation.Deserializer.DEFAULT_TRANSLATION)) {
            jsonObject.add("translation", writeVector3fc(transformation.rotation(), 16));
        }
        if (!transformation.scale().equals(Transformation.Deserializer.DEFAULT_SCALE)) {
            jsonObject.add("scale", writeVector3fc(transformation.rotation()));
        }
        return jsonObject;
    }

    private static JsonArray writeVector3fc(Vector3fc vector) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(vector.x());
        jsonArray.add(vector.y());
        jsonArray.add(vector.z());
        return jsonArray;
    }

    private static JsonArray writeVector3fc(Vector3fc vector, int scale) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(vector.x() * scale);
        jsonArray.add(vector.y() * scale);
        jsonArray.add(vector.z() * scale);
        return jsonArray;
    }

    private static JsonArray writeElements(List<ModelElement> elements) {
        JsonArray jsonArray = new JsonArray();
        for (ModelElement element : elements) {
            jsonArray.add(writeElement(element));
        }
        return jsonArray;
    }

    private static JsonObject writeElement(ModelElement element) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("from", writeVector3fc(element.from()));
        jsonObject.add("to", writeVector3fc(element.to()));
        if (element.rotation() != null) {
            jsonObject.add("rotation", writeModelRotation(element.rotation()));
        }
        if (!element.shade()) {
            jsonObject.addProperty("shade", false);
        }
        jsonObject.add("faces", writeFaces(element.faces()));
        return jsonObject;
    }

    private static JsonObject writeModelRotation(ModelRotation rotation) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("origin", writeVector3fc(rotation.origin(), 16));
        jsonObject.addProperty("axis", rotation.axis().name());
        jsonObject.addProperty("angle", rotation.angle());
        if (rotation.rescale()) {
            jsonObject.addProperty("rescale", true);
        }
        return jsonObject;
    }

    private static JsonObject writeFaces(Map<Direction, ModelElementFace> faces) {
        JsonObject jsonObject = new JsonObject();
        faces.forEach((direction, face) -> {
            jsonObject.add(direction.name(), writeFace(face));
        });
        return jsonObject;
    }

    private static JsonObject writeFace(ModelElementFace face) {
        JsonObject jsonObject = new JsonObject();
        if (face.uvs() != null) {
            jsonObject.add("uv", writeUVs(face.uvs()));
        }
        jsonObject.addProperty("texture", face.textureId());
        if (face.cullFace() != null) {
            jsonObject.addProperty("cullface", face.cullFace().name());
        }
        if (face.rotation().ordinal() != 0) {
            jsonObject.addProperty("rotation", face.rotation().ordinal());
        }
        if (face.tintIndex() != -1) {
            jsonObject.addProperty("tintindex", face.tintIndex());
        }
        return jsonObject;
    }

    private static JsonArray writeUVs(ModelElementFace.UV uvs) {
        JsonArray jsonArray = new JsonArray(4);
        for (float uv : new float[]{uvs.minU(), uvs.minV(), uvs.maxU(), uvs.maxV()}) {
            jsonArray.add(uv);
        }
        return jsonArray;
    }

    public static class Deserializer implements JsonDeserializer<JsonModel> {
        private final Function<Identifier, Optional<JsonModel>> modelProvider;

        public Deserializer(Function<Identifier, Optional<JsonModel>> modelProvider) {
            this.modelProvider = modelProvider;
        }

        @Override
        public JsonModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> elements = this.elementsFromJson(jsonDeserializationContext, jsonObject);
            Optional<Identifier> parent = this.parentFromJson(jsonObject);
            Optional<JsonModel> parentModel = parent.flatMap(modelProvider);

            Map<String, TextureKey> textureKeyMap = parentModel
                    .map(JsonModel::copyTextureKeyMap)
                    .orElseGet(HashMap::new);


            TextureMap textures = this.texturesFromJson(jsonObject, parentModel, textureKeyMap);
            Optional<Boolean> ambientOcclusion = this.ambientOcclusionFromJson(jsonObject);

            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
            }
            // can't be used in block models
            //List<ModelOverride> modelOverrides = this.overridesFromJson(jsonDeserializationContext, jsonObject);

            /* can't be used in block models
            Optional<JsonUnbakedModel.GuiLight> guiLight;
            if (jsonObject.has("gui_light")) {
                guiLight = Optional.of(JsonUnbakedModel.GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light")));
            } else {
                guiLight = Optional.empty();
            }
             */

            TextureKey[] requiredTextures = textureKeyMap.values().toArray(new TextureKey[0]);

            return new JsonModel(parent, Optional.empty(), elements, textures, ambientOcclusion, /*modelOverrides,*/ modelTransformation/*, guiLight*/, requiredTextures);
        }

        protected List<ModelElement> elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            ArrayList<ModelElement> list = Lists.newArrayList();
            if (json.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add(context.deserialize(jsonElement, ModelElement.class));
                }
            }
            return list;
        }

        private Optional<Identifier> parentFromJson(JsonObject json) {
            return Optional.of(JsonHelper.getString(json, "parent", ""))
                    .filter(Predicate.not(String::isEmpty))
                    .map(Identifier::of);
        }

        private TextureMap texturesFromJson(JsonObject object, Optional<JsonModel> parentModel, Map<String, TextureKey> textureKeyMap) {
            TextureMap textures = new TextureMap();
            parentModel.map(JsonModel::getTextureMap)
                    .ifPresent((parentTextureMap) -> {
                        textures.entries.putAll(parentTextureMap.entries);
                        textures.inherited.addAll(parentTextureMap.inherited);
                    });

            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    String reference = entry.getValue().getAsString();
                    TextureKey textureKey;
                    if (reference.charAt(0) == '#') {
                        TextureKey parent = Objects.requireNonNull(textureKeyMap.get(reference.substring(1)));
                        textureKey = TextureKey.of(key, parent);
                        textures.inherit(parent, textureKey);
                    } else {
                        textureKey = TextureKey.of(key, null);
                        textures.put(textureKey, Identifier.of(reference));
                    }
                    textureKeyMap.put(key, textureKey);
                }
            }
            return textures;
        }

        private Optional<Boolean> ambientOcclusionFromJson(JsonObject json) {
            return json.has("ambientocclusion") ? Optional.of(json.get("ambientocclusion").getAsBoolean()) : Optional.empty();
        }
        /* can't be used in block models
        private List<ModelOverride> overridesFromJson(JsonDeserializationContext context, JsonObject object) {
            ArrayList<ModelOverride> list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add(context.deserialize(jsonElement, ModelOverride.class));
                }
            }
            return list;
        }
         */
    }
}
