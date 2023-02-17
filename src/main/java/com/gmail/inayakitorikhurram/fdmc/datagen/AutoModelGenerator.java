package com.gmail.inayakitorikhurram.fdmc.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.List;
import java.util.Map;

public class AutoModelGenerator extends FabricModelProvider {
    private final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

    public AutoModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Map<Identifier, List<Resource>> stuff = ModelLoader.BLOCK_STATES_FINDER.findAllResources(resourceManager);
        System.out.println();
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    private void autoGenModels(BlockStateModelGenerator blockStateModelGenerator, Block block) {

    }
}
