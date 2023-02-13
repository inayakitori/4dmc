package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.RedstoneTorchBlock.LIT;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin extends TorchBlockMixin{
    //todo modiy this thing's updates
}
