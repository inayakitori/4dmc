package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.MultiInventory;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EnclosingInstanceAccess;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ViewerCountManager.class)
public class ViewerCountManagerMixin implements EnclosingInstanceAccess<ChestBlockEntity> {

    private ChestBlockEntity this$0;

    @Redirect(method = "getViewingUsers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box useBox4(Box instance, double value){
        Box4 box4 = Box4.converted(instance).expand(value, value + 1);
        //FDMCConstants.LOGGER.info("box3 {} --> box4 {}", instance, box4);
        return box4;
    }




    @Override
    public void setEnclosingInstance(ChestBlockEntity enclosingInstance) {
        this$0 = enclosingInstance;
    }

    @Override
    public ChestBlockEntity getEnclosingInstance() {
        return this$0;
    }
}
