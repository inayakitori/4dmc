package com.gmail.inayakitorikhurram.fdmc.client.model;

import net.minecraft.client.model.*;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ModelCuboidDatas {

    private static final int[] DIRECTION_ID_TO_TEXTURE_ID = new int[]{
            2, //down
            3, //up
            4, //north
            5, //south
            1, //west
            0  //east
    };

    private static void cloneSide(ModelPart.Cuboid cuboid, Direction dir, Direction sample) {
        int sampleIndex = DIRECTION_ID_TO_TEXTURE_ID[sample.getId()];
        int dirIndex = DIRECTION_ID_TO_TEXTURE_ID[dir.getId()];
        cloneUV(cuboid.sides[dirIndex], cuboid.sides[sampleIndex]);
    }

    //clones UV of 2 into 1
    private static void cloneUV(ModelPart.Quad quad, ModelPart.Quad sample){

        final int[] quadToSampleQuad = new int[]{
                1, // --> 0
                0, // --> 1
                3, // --> 2
                2  // --> 3
        };

        for(int i = 0; i < 4 ; i++){
            //0 clones from 3, 1 clones from 2
            //0 clones from
            quad.vertices[i].u = sample.vertices[quadToSampleQuad[i]].u;
            quad.vertices[i].v = sample.vertices[quadToSampleQuad[i]].v;
        }
    }


    public static class BackMirroredModelCuboidData extends ModelCuboidData {

        public BackMirroredModelCuboidData(@Nullable String name, float textureX, float textureY, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Dilation extra, boolean mirror, float textureScaleX, float textureScaleY) {
            super(name, textureX, textureY, offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, extra, mirror, textureScaleX, textureScaleY);
        }

        @Override
        public ModelPart.Cuboid createCuboid(int textureWidth, int textureHeight) {
            ModelPart.Cuboid cuboid = super.createCuboid(textureWidth, textureHeight);

            cloneSide(cuboid, Direction.SOUTH, Direction.NORTH);

            return cuboid;
        }

        public static ModelPartData create(String name, ModelPartData modelPartData, ModelPartBuilder builder, ModelTransform rotationData){

            ModelPartBuilder modifiedBuilder = ModelPartBuilder.create()
                    .uv(builder.textureX, builder.textureY)
                    .mirrored(builder.mirror);

            for(int i = 0; i < builder.cuboidData.size(); i++){
                ModelCuboidData data = builder.cuboidData.get(i);
                modifiedBuilder.cuboidData.add(new BackMirroredModelCuboidData(
                        data.name,
                        builder.textureX, builder.textureY,
                        data.offset.x, data.offset.y, data.offset.z,
                        data.dimensions.x, data.dimensions.y, data.dimensions.z,
                        Dilation.NONE,
                        builder.mirror,
                        1.0f, 1.0f)
                );
            }

            return modelPartData.addChild(name, modifiedBuilder, rotationData);
        }

    }

    public static class BackSideMirroredCuboidData extends ModelCuboidData {

        public BackSideMirroredCuboidData(@Nullable String name, float textureX, float textureY, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Dilation extra, boolean mirror, float textureScaleX, float textureScaleY) {
            super(name, textureX, textureY, offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, extra, mirror, textureScaleX, textureScaleY);
        }

        @Override
        public ModelPart.Cuboid createCuboid(int textureWidth, int textureHeight) {
            ModelPart.Cuboid cuboid = super.createCuboid(textureWidth, textureHeight);

            cloneSide(cuboid, Direction.SOUTH, Direction.NORTH);
            cloneSide(cuboid, Direction.SOUTH, Direction.EAST);
            cloneSide(cuboid, Direction.WEST, Direction.SOUTH);
            return cuboid;
        }

        public static ModelPartData create(String name, ModelPartData modelPartData, ModelPartBuilder builder, ModelTransform rotationData){

            ModelPartBuilder modifiedBuilder = ModelPartBuilder.create()
                    .uv(builder.textureX, builder.textureY)
                    .mirrored(builder.mirror);

            for(int i = 0; i < builder.cuboidData.size(); i++){
                ModelCuboidData data = builder.cuboidData.get(i);
                modifiedBuilder.cuboidData.add(new BackSideMirroredCuboidData(
                        data.name,
                        builder.textureX, builder.textureY,
                        data.offset.x, data.offset.y, data.offset.z,
                        data.dimensions.x, data.dimensions.y, data.dimensions.z,
                        Dilation.NONE,
                        builder.mirror,
                        1.0f, 1.0f)
                );
            }

            return modelPartData.addChild(name, modifiedBuilder, rotationData);
        }

    }



}
