package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @ModifyArg(method = {
            "createStoneButtonBlock",
            "createWoodenButtonBlock(Lnet/minecraft/sound/BlockSoundGroup;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundEvent;)Lnet/minecraft/block/ButtonBlock;"
    },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/ButtonBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;IZLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundEvent;)V"
            )
    )
    private static AbstractBlock.Settings modifySettingsButtons(AbstractBlock.Settings settings){
        return MixinUtil.enableAllWCapabilities(settings);
    }
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsRedstoneWire(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RepeaterBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsRepeater(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ObserverBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsRedstoneObserver(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HopperBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsHopper(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComparatorBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsComparator(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneTorchBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsRedstoneTorch(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/WallRedstoneTorchBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsWallRedstoneTorch(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    // necessary since WallRedstoneTorchBlock steals the torch blocks placement logic
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/WallTorchBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;Lnet/minecraft/particle/ParticleEffect;)V"))
    private static AbstractBlock.Settings modifySettingsWallTorch(AbstractBlock.Settings settings) {
        return MixinUtil.use4DProperties(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SandBlock;<init>(ILnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsSand(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/GravelBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsGravel(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ScaffoldingBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsScaffolding(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/EndRodBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsEndRod(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilitiesAndGetSideW(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/NetherPortalBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings modifySettingsNetherPortal(AbstractBlock.Settings settings) {
        return MixinUtil.enableAllWCapabilities(settings);
    }

    //water
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidBlock;<init>(Lnet/minecraft/fluid/FlowableFluid;Lnet/minecraft/block/AbstractBlock$Settings;)V", ordinal = 0))
    private static AbstractBlock.Settings modifySettingsWater(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }

    //lava
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidBlock;<init>(Lnet/minecraft/fluid/FlowableFluid;Lnet/minecraft/block/AbstractBlock$Settings;)V", ordinal = 1))
    private static AbstractBlock.Settings modifySettingsLava(AbstractBlock.Settings settings) {
        return MixinUtil.acceptWNeighbourUpdates(settings);
    }
}
