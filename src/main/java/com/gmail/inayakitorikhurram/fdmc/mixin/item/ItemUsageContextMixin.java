package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {

    @Shadow @Final private @Nullable PlayerEntity player;


    @Mutable
    @Shadow @Final private BlockHitResult hit;

    //if the player is trying to place/face w then let them.
    @Inject(method = "getHorizontalPlayerFacing", at = @At("RETURN"), cancellable = true)
    public void getHorizontalPlayerFacing(CallbackInfoReturnable<Direction> cir) {
        if (!((ItemUsageContext)(Object)this instanceof ItemPlacementContext4)) return;
        CanStep.of(this.player).flatMap(CanStep::getPlacementDirection4).ifPresent((direction -> {
            cir.setReturnValue(direction);
            cir.cancel();
        }));
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/hit/BlockHitResult;)V", at = @At("TAIL"))
    private void initAllowSidePlacement(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult, CallbackInfo ci){
        if (!(playerEntity instanceof CanStep steppingPlayer)) return;
        if (playerEntity.shouldCancelInteraction()) return;
        Optional<Direction> placementSide = steppingPlayer.getPlacementDirection4();
        if (placementSide.isEmpty()) return;

        //if the player is trying to place a block adjacent to the current blocks position, allow that offset
        Vec4d hitPos = new Vec4d(blockHitResult.getPos());
        Vec3d newPlacementPos = hitPos
                .withBias(
                        Direction4.asDirection4(placementSide.get()),
                        placementSide.get().getDirection().offset()
                ).toPos3();
        this.hit = new BlockHitResult(
                newPlacementPos,
                placementSide.get(),
                blockHitResult.getBlockPos(),
                blockHitResult.isInsideBlock()
        );

//        FDMCConstants.LOGGER.info("ItemUsageContext hand: {} item: {} hit: {},{} placement: {}",
//                hand,
//                itemStack,
//                this.hit.getBlockPos(),
//                this.hit.getSide(),
//                placementSide.get()
//        );
    }
}
