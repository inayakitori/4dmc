package com.gmail.inayakitorikhurram.fdmc.screen;

import com.gmail.inayakitorikhurram.fdmc.FDMCMainEntrypoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

//Does not extend GenericCOntaineereScreenHandler because only wants some of it's functionality and not others
public class FDMCScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final int rows;

    private final int columns;

    public FDMCScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, int rows, int columns) {
        this(type, syncId, playerInventory, new SimpleInventory(columns * rows), rows, columns);
    }

    public static FDMCScreenHandler createGeneric9x12(int syncId, PlayerInventory playerInventory) {
        return new FDMCScreenHandler(FDMCMainEntrypoint.getGeneric9x12(), syncId, playerInventory, 6, 18);
    }


    public static FDMCScreenHandler createGeneric9x12(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new FDMCScreenHandler(FDMCMainEntrypoint.getGeneric9x12(), syncId, playerInventory, inventory, 6, 18);
    }



    public FDMCScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, int columns) {
        super(type, syncId);
        FDMCScreenHandler.checkSize(inventory, rows * columns);
        this.inventory = inventory;
        this.rows = rows;
        this.columns = columns;
        inventory.onOpen(playerInventory.player);
        int playerInventoryYOffset = 11 + (this.rows - 4) * 18;
        int playerInventoryXOffset = 1 + (this.columns - 9) * 18 / 2;
        //chest inventory
        for (int slotRow = 0; slotRow < this.rows; ++slotRow) {
            for (int slotColumn = 0; slotColumn < this.columns; ++slotColumn) {
                int chestRow = Math.floorDiv(slotColumn, 9);
                int slotIndex = (slotColumn % 9) + slotRow * 9 + chestRow * 9 * this.rows;

                this.addSlot(new Slot(inventory, slotIndex, 8 + slotColumn * 18, 18 + slotRow * 18));
            }
        }
        //player inventory
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, playerInventoryXOffset + 8 + k * 18, 103 + j * 18 + playerInventoryYOffset));
            }
        }
        //player hotbar
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, playerInventoryXOffset + 8 + j * 18, 161 + playerInventoryYOffset));
        }
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * this.columns ? !this.insertItem(itemStack2, this.rows * this.columns, this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.rows * this.columns, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }
    public int getColumns() {
        return this.columns;
    }

}
