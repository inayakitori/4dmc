package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;


@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends AbstractRedstoneGateBlockMixin {

    @Shadow @Final public static EnumProperty<ComparatorMode> MODE;

    @Shadow protected abstract int getPower(World world, BlockPos pos, BlockState state);

    @Shadow @Nullable protected abstract ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos);


    @Redirect(method = "*", at=@At(value = "FIELD", target = "Lnet/minecraft/block/ComparatorBlock;FACING:Lnet/minecraft/state/property/DirectionProperty;"))
    private DirectionProperty fdmc$redirectFacingProperty(){
        return HORIZONTAL_FACING4;
    }


}
