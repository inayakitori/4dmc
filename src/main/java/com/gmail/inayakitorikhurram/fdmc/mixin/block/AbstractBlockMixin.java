package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin implements BlockSettings4Access {
    @Unique
    private boolean use4dProperties = false;
    @Unique
    private boolean acceptsWNeighbourUpdates = false;
    @Unique
    private boolean useGetSideW = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSettings(AbstractBlock.Settings settings, CallbackInfo ci) {
        BlockSettings4Access blockSettings4 = (BlockSettings4Access) settings;

        this.use4dProperties = blockSettings4.uses4DProperties();
        this.acceptsWNeighbourUpdates = blockSettings4.acceptsWNeighbourUpdates();
        this.useGetSideW = blockSettings4.useGetSideW();
    }



    @Override
    public boolean uses4DProperties() {
        return this.use4dProperties;
    }

    @Override
    public boolean acceptsWNeighbourUpdates() {
        return this.acceptsWNeighbourUpdates;
    }

    @Override
    public boolean useGetSideW() {
        return this.useGetSideW;
    }

    @Mixin(AbstractBlock.Settings.class)
    public static class AbstractBlockSettingsMixin implements BlockSettings4, BlockSettings4Access {
        private boolean use4dProperties = false;
        private boolean acceptsWNeighbourUpdates = false;
        private boolean useGetSideW = false;


        @Inject(method = "copyShallow", at = @At("RETURN"), cancellable = true)
        private static void initSettings(AbstractBlock block, CallbackInfoReturnable<AbstractBlock.Settings> cir) {
            BlockSettings4 copiedSettings = BlockSettings4.asBlockSettings4(cir.getReturnValue());
            BlockSettings4Access templateSettings = (BlockSettings4Access) block.getSettings();

            copiedSettings = copiedSettings.use4DProperties(templateSettings.uses4DProperties());
            copiedSettings = copiedSettings.acceptsWNeighbourUpdates(templateSettings.acceptsWNeighbourUpdates());
            copiedSettings = copiedSettings.useGetSideW(templateSettings.useGetSideW());
            cir.setReturnValue((AbstractBlock.Settings) copiedSettings);
        }

        @Override
        public BlockSettings4 use4DProperties(boolean value) {
            this.use4dProperties = value;
            return this;
        }

        @Override
        public boolean uses4DProperties() {
            return use4dProperties;
        }

        @Override
        public BlockSettings4 acceptsWNeighbourUpdates(boolean value) {
            this.acceptsWNeighbourUpdates = value;
            return this;
        }

        @Override
        public BlockSettings4 useGetSideW(boolean value) {
            this.useGetSideW = value;
            return this;
        }

        @Override
        public boolean acceptsWNeighbourUpdates() {
            return acceptsWNeighbourUpdates;
        }

        @Override
        public boolean useGetSideW() {
            return this.useGetSideW;
        }
    }
}
