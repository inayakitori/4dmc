package com.gmail.inayakitorikhurram.fdmc.math;

import com.ibm.icu.impl.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Arrays;


//for a list of inventories
public class MultiInventory
        implements Inventory {
    private final Inventory[] inventories;

    public MultiInventory(Inventory... inventories) {
        this.inventories = Arrays.copyOf(inventories, inventories.length);
    }

    @Override
    public int size() {
        int size = 0;
        for (Inventory inv : inventories){
            size += inv.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return inventories.length == 0;
    }

    public boolean isPart(Inventory inventory) {
        for (Inventory inv : inventories){
            if(inv == inventory) return true;
        }
        return false;
    }

    private Pair<Inventory, Integer> getInventoryAndSlot(int slot){
        int invIndex = 0;
        //decrease the slots and increase the inventory while moving through
        while(slot >= inventories[invIndex].size()){
            slot -= inventories[invIndex].size();
            invIndex ++;
        }
        return Pair.of(inventories[invIndex], slot);
    }

    @Override
    public ItemStack getStack(int slot) {
        Pair<Inventory, Integer> pair = getInventoryAndSlot(slot);
        return pair.first.getStack(pair.second);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        Pair<Inventory, Integer> pair = getInventoryAndSlot(slot);
        return pair.first.removeStack(pair.second, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        Pair<Inventory, Integer> pair = getInventoryAndSlot(slot);
        return pair.first.removeStack(pair.second);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        Pair<Inventory, Integer> pair = getInventoryAndSlot(slot);
        pair.first.setStack(pair.second, stack);
    }

    @Override
    public int getMaxCountPerStack() {
        return inventories[0].getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        for(Inventory inv: inventories){
            inv.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        for(Inventory inv: inventories){
            if(!inv.canPlayerUse(player)) return false;
        }
        //else
        return true;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        for(Inventory inv: inventories){
            inv.onOpen(player);
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        for(Inventory inv: inventories){
            inv.onClose(player);
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        Pair<Inventory, Integer> pair = getInventoryAndSlot(slot);
        return pair.first.isValid(pair.second, stack);
    }

    @Override
    public void clear() {
        for(Inventory inv: inventories){
            inv.clear();
        }
    }
}

