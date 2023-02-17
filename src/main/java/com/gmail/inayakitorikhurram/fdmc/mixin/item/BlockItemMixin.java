package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4Access;
import net.minecraft.block.Block;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    // This works correctly, IntelliJ is wrong.
    @Redirect(method = "useOnBlock", at = @At(value = "NEW", target = "Lnet/minecraft/item/ItemPlacementContext;"))
    private ItemPlacementContext useOnBlockCheck4D(ItemUsageContext context) { // TODO: AutomaticItemPlacementContext4
        ItemSettings4Access settings = ((ItemSettings4Access) this);
        if ((settings.uses4DProperties())&& !(context instanceof AutomaticItemPlacementContext)) {
            return new ItemPlacementContext4(context).useGetSideW(settings.useGetSideW());
        }
        return new ItemPlacementContext(context);
    }
}
