package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.math.MultiInventory;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin {
    @Final
    @Shadow private ViewerCountManager stateManager;

    @Mixin(targets = "net.minecraft.block.entity.ChestBlockEntity$1")
    private abstract static class ViewerCountManagerMixin{


        @Final @Shadow(aliases = "field_27211") ChestBlockEntity this$0; // ChestBlockEntity.this


        @WrapMethod(method = "isPlayerViewing")
        private boolean modifyViewingPredicate(PlayerEntity player, Operation<Boolean> this$isPlayerViewing){
            boolean isPlayerViewing4;
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                isPlayerViewing4 =  inventory == this$0 || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(this$0);
            } else if (player.currentScreenHandler instanceof FDMCScreenHandler){
                Inventory inventory = ((FDMCScreenHandler)player.currentScreenHandler).getInventory();
                isPlayerViewing4 =  inventory == this$0 || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(this$0) || inventory instanceof MultiInventory && ((MultiInventory)inventory).isPart(this$0);
            } else {
                isPlayerViewing4 = false;
            }

            return this$isPlayerViewing.call(player) || isPlayerViewing4;
        }

    }

}
