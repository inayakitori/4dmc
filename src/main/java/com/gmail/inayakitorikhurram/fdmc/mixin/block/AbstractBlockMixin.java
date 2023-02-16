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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSettings(AbstractBlock.Settings settings, CallbackInfo ci) {
        BlockSettings4Access blockSettings4 = (BlockSettings4Access) settings;

        this.use4dProperties = blockSettings4.uses4DProperties();
        this.acceptsWNeighbourUpdates = blockSettings4.acceptsWNeighbourUpdates();
    }

    @Override
    public boolean uses4DProperties() {
        return use4dProperties;
    }

    @Override
    public boolean acceptsWNeighbourUpdates() {
        return acceptsWNeighbourUpdates;
    }

    @Mixin(AbstractBlock.Settings.class)
    public abstract static class AbstractBlockSettingsMixin implements BlockSettings4, BlockSettings4Access {
        private boolean use4dProperties = false;
        private boolean acceptsWNeighbourUpdates = false;

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
            return null;
        }

        @Override
        public boolean acceptsWNeighbourUpdates() {
            return acceptsWNeighbourUpdates;
        }
    }
}
