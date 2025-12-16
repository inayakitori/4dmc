package com.gmail.inayakitorikhurram.fdmc.client.render.block.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import org.joml.Vector3f;

public class ChestWBlockModel extends ChestBlockModel {
    private static final float LID_PIVOT_Y = 9f;
    public ChestWBlockModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setAngles(Float animationProgress) {
        super.lid.originY = LID_PIVOT_Y + animationProgress * 6f;
    }
}
