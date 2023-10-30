package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @ModifyArg(method = {
            "createStoneButtonBlock",
            "createWoodenButtonBlock(Lnet/minecraft/block/BlockSetType;[Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Lnet/minecraft/block/ButtonBlock;"
    },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/ButtonBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;Lnet/minecraft/block/BlockSetType;IZ)V"
            )
    )
    private static AbstractBlock.Settings modifySettingsButtons(AbstractBlock.Settings settings){
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyExpressionValue(method = "<clinit>", slice = { // "to" not needed, just use ordinal = 0 for the injector "at"s
            @Slice(id = "redstone_wire", from = @At(value = "CONSTANT", args = "stringValue=redstone_wire")),
            @Slice(id = "repeater", from = @At(value = "CONSTANT", args = "stringValue=repeater")),
            @Slice(id = "observer", from = @At(value = "CONSTANT", args = "stringValue=observer")),
            @Slice(id = "comparator", from = @At(value = "CONSTANT", args = "stringValue=comparator")),
            @Slice(id = "redstone_wall_torch", from = @At(value = "CONSTANT", args = "stringValue=redstone_wall_torch")),
            @Slice(id = "scaffolding", from = @At(value = "CONSTANT", args = "stringValue=scaffolding")),
            @Slice(id = "nether_portal", from = @At(value = "CONSTANT", args = "stringValue=nether_portal")),
            @Slice(id = "water", from = @At(value = "CONSTANT", args = "stringValue=water")),
            @Slice(id = "lava", from = @At(value = "CONSTANT", args = "stringValue=lava")),
            @Slice(id = "chest", from = @At(value = "CONSTANT", args = "stringValue=chest")),
            @Slice(id = "trapped_chest", from = @At(value = "CONSTANT", args = "stringValue=trapped_chest"))
    }, at = {
            @At(slice = "redstone_wire", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "repeater", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "observer", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "comparator", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "redstone_wall_torch", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "scaffolding", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "nether_portal", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "water", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "lava", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "chest", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "trapped_chest", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0)
    })
    private static AbstractBlock.Settings enableAllWCapabilities(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyExpressionValue(method = "<clinit>", slice = { // "to" not needed, just use ordinal = 0 for the injector "at"s
            @Slice(id = "hopper", from = @At(value = "CONSTANT", args = "stringValue=hopper")),
            @Slice(id = "end_rod", from = @At(value = "CONSTANT", args = "stringValue=end_rod"))
    }, at = {
            @At(slice = "hopper", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "end_rod", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0)
    })
    private static AbstractBlock.Settings enableAllWCapabilitiesAndGetSideW(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilitiesAndGetSideW(settings);
    }

    @ModifyExpressionValue(method = "<clinit>", slice = { // "to" not needed, just use ordinal = 0 for the injector "at"s
            @Slice(id = "redstone_torch", from = @At(value = "CONSTANT", args = "stringValue=redstone_torch")),
            @Slice(id = "sand", from = @At(value = "CONSTANT", args = "stringValue=sand")),
            @Slice(id = "red_sand", from = @At(value = "CONSTANT", args = "stringValue=red_sand")),
            @Slice(id = "gravel", from = @At(value = "CONSTANT", args = "stringValue=gravel"))
    }, at = {
            @At(slice = "redstone_torch", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "sand", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "red_sand", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0),
            @At(slice = "gravel", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0)
    })
    private static AbstractBlock.Settings acceptWNeighbourUpdates(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }

    // necessary since WallRedstoneTorchBlock steals the torch blocks placement logic
    @ModifyExpressionValue(method = "<clinit>", slice = { // "to" not needed, just use ordinal = 0 for the injector "at"s
            @Slice(id = "wall_torch", from = @At(value = "CONSTANT", args = "stringValue=wall_torch"))
    }, at = {
            @At(slice = "wall_torch", target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;", value = "INVOKE", ordinal = 0)
    })
    private static AbstractBlock.Settings use4DProperties(AbstractBlock.Settings settings) {
        return MixinUtil.use4DProperties(settings);
    }
}
