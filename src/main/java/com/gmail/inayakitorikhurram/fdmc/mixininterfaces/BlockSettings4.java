package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.block.AbstractBlock;

public interface BlockSettings4 {
    static BlockSettings4 asBlockSettings4(AbstractBlock.Settings settings) {
        return (BlockSettings4) settings;
    }

    default AbstractBlock.Settings asBlockSettings() {
        return (AbstractBlock.Settings) this;
    }

    BlockSettings4 use4DProperties(boolean value);

    BlockSettings4 acceptsWNeighbourUpdates(boolean value);

    BlockSettings4 useGetSideW(boolean value);
}
