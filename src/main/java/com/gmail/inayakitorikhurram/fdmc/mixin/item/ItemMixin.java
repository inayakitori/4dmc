package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4Access;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements ItemSettings4Access {
    private boolean use4dProperties = false;
    private boolean useGetSideW = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSettings(Item.Settings settings, CallbackInfo ci) {
        ItemSettings4Access itemSettings4 = (ItemSettings4Access) settings;

        this.use4dProperties = itemSettings4.uses4DProperties();
        this.useGetSideW = itemSettings4.useGetSideW();
    }

    @Override
    public boolean uses4DProperties() {
        return this.use4dProperties;
    }

    @Override
    public boolean useGetSideW() {
        return this.useGetSideW;
    }


    @Mixin(Item.Settings.class)
    public static class ItemSettingsMixin implements ItemSettings4, ItemSettings4Access{
        private boolean use4dProperties = false;
        private boolean useGetSideW = false;

        @Override
        public ItemSettings4 use4DProperties(boolean value) {
            this.use4dProperties = value;
            return this;
        }

        @Override
        public ItemSettings4 useGetSideW(boolean value) {
            this.useGetSideW = value;
            return this;
        }

        @Override
        public boolean uses4DProperties() {
            return this.use4dProperties;
        }

        @Override
        public boolean useGetSideW() {
            return this.useGetSideW;
        }
    }
}
