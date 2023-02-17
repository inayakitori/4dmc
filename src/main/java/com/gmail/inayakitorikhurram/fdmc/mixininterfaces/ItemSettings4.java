package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.item.Item;

public interface ItemSettings4 {
    static ItemSettings4 asItemSettings4(Item.Settings settings) {
        return (ItemSettings4) settings;
    }

    default Item.Settings asItemSettings() {
        return (Item.Settings) this;
    }

    ItemSettings4 use4DProperties(boolean value);

    ItemSettings4 useGetSideW(boolean value);

    default ItemSettings4 apply(BlockSettings4Access blockSettings4) {
        return this
                .use4DProperties(blockSettings4.uses4DProperties())
                .useGetSideW(blockSettings4.useGetSideW());
    }
}
