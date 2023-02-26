package com.gmail.inayakitorikhurram.fdmc.screen;

import com.gmail.inayakitorikhurram.fdmc.FDMCMainEntrypoint;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;



public class FDMCScreenHandler extends GenericContainerScreenHandler{


    private FDMCScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, int rows) {
        this(type, syncId, playerInventory, new SimpleInventory(9 * rows), rows);
    }

    public FDMCScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    public static FDMCScreenHandler createGeneric9x12(int syncId, PlayerInventory playerInventory) {
        return new FDMCScreenHandler(FDMCMainEntrypoint.getGeneric9x12(), syncId, playerInventory, 12);
    }


    public static FDMCScreenHandler createGeneric9x12(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new FDMCScreenHandler(FDMCMainEntrypoint.getGeneric9x12(), syncId, playerInventory, inventory, 12);
    }

}
