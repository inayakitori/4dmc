package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
public abstract class ItemUsageContextMixin {

    @Shadow @Final private @Nullable PlayerEntity player;


    @Mutable
    @Shadow @Final private BlockHitResult hit;

    @Shadow
    public abstract boolean shouldCancelInteraction();

    //if the player is trying to place/face w then let them.
    @Inject(method = "getHorizontalPlayerFacing", at = @At("RETURN"), cancellable = true)
    public void getHorizontalPlayerFacing(CallbackInfoReturnable<Direction> cir) {
        if (!((ItemUsageContext)(Object)this instanceof ItemPlacementContext4)) return;
        CanPlaceW.of(this.player).flatMap(CanPlaceW::getPlacementDirection4).ifPresent((direction -> {
            cir.setReturnValue(direction);
            cir.cancel();
        }));
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/hit/BlockHitResult;)V", at = @At("TAIL"))
    private void initAllowSidePlacement(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult, CallbackInfo ci){
        if (!(playerEntity instanceof CanPlaceW steppingPlayer)) return;
        if (MixinUtil.shouldShiftInteractionW(playerEntity)) return;
        Optional<Direction> placementSide = steppingPlayer.getPlacementDirection4();
        if (placementSide.isEmpty()) return;

        //if the player is trying to place a block adjacent to the current blocks position, allow that offset
        Vec4d hitPos = Vec4d.of(blockHitResult.getPos());
        Vec3d newPlacementPos = hitPos
                .offset(
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
