package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.DistancePredicateI;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FDMCAdvancementGenerator extends FabricAdvancementProvider {
    protected FDMCAdvancementGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
        int sliceTravelDistance = 200;
        AdvancementEntry hyperspaceBubble = Advancement.Builder.create()
                .display(Items.FILLED_MAP,
                        Text.literal("Hyperspace Bubble"),
                        Text.literal("Use the Nether to travel " + sliceTravelDistance + " slices in the Overworld"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .parent(Identifier.ofVanilla("nether/fast_travel"))
                .criterion("travelled_w", TravelCriterion.Conditions.netherTravel(DistancePredicateI.w(NumberRange.DoubleRange.atLeast(sliceTravelDistance))))
                .build(Identifier.of("fdmc", "hyperspace_bubble"));
        consumer.accept(hyperspaceBubble);

    }


}
