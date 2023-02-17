package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin implements BlockSettings4Access {
    private boolean use4dProperties = false;
    private boolean acceptsWNeighbourUpdates = false;
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
