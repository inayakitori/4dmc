package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.OxidizableCopperChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OxidizableCopperChestBlock.class)
public abstract class OxidizableCopperChestBlockMixin
        extends CopperChestBlock
        implements Oxidizable {
    public OxidizableCopperChestBlockMixin(OxidationLevel oxidationLevel, SoundEvent openSound, SoundEvent closeSound, Settings settings) {
        super(oxidationLevel, openSound, closeSound, settings);
    }

    // it's annoying to modify the if statement so this is basically the same
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/OxidizableCopperChestBlock;tickDegradation(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V"))
    private void fdmc$checkType2(OxidizableCopperChestBlock chest, BlockState state, ServerWorld serverWorld, BlockPos blockPos, Random random, Operation<Void> this$tickDegradation){
        if (!state.get(FDMCProperties.CHEST_TYPE_2).equals(ChestType.RIGHT)) {
            this$tickDegradation.call(chest, state, serverWorld, blockPos, random);
        } else {
            //do nothing.
        }
    }
}
