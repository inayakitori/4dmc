package com.gmail.inayakitorikhurram.fdmc.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ModelCuboidDatas {

    private static void cloneSide(ModelPart.Cuboid cuboid, Direction dir, Direction sample){
        int sampleIndex = sample.getId();
        int dirIndex = dir.getId();
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

            cloneSide(cuboid, Direction.EAST, Direction.WEST);

            return cuboid;
        }
    }
}
