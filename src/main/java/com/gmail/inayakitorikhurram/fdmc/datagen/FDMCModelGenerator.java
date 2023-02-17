package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.resource.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FDMCModelGenerator extends FabricModelProvider {
    public FDMCModelGenerator(FabricDataOutput output) {
        super(output);
    }

    private void createButton4(Identifier texture, Block block, BlockStateModelGenerator blockStateModelGenerator){
        TextureMap textureMap = TextureMap.texture(texture);

        //what we append to for every blockstate variant
        MultipartBlockStateSupplier blockStateSupplier = MultipartBlockStateSupplier.create(block);

        for(boolean powered : new boolean[]{true, false}) {
            for (Direction direction : new Direction[]{Direction4Constants.KATA, Direction4Constants.ANA}) {

                String variant = "_" + direction.getName() + (powered? "_powered" : "");

                Model model = new Model(
                        Optional.of(
                                new Identifier("minecraft", "block/button" + variant)
                        ),
                        Optional.of(variant),
                        TextureKey.TEXTURE
                );

                Identifier identifier = model.upload(block, textureMap, blockStateModelGenerator.modelCollector);

                blockStateSupplier = blockStateSupplier.with(
                        When.create()
                                .set(Properties.HORIZONTAL_FACING, direction)
                                .set(Properties.POWERED, powered),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, identifier)
                );

            }
        }
        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //TODO use a json parser ot get the texture
        createButton4(Identifier.of("minecraft", "block/acacia_planks"), Blocks.ACACIA_BUTTON, blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //none
    }
}
