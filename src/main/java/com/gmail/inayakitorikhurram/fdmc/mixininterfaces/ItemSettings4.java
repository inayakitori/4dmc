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
}
