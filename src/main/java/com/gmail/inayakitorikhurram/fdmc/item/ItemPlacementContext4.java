package com.gmail.inayakitorikhurram.fdmc.item;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemPlacementContext4 extends ItemPlacementContext {
    public ItemPlacementContext4(PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hitResult) {
        super(player, hand, stack, hitResult);
    }

    public ItemPlacementContext4(ItemUsageContext context) {
        super(context);
    }

    public ItemPlacementContext4(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
        super(world, playerEntity, hand, itemStack, blockHitResult);
    }

    @Override
    public Direction getPlayerLookDirection() {
        return CanStep.of(getPlayer())
                .flatMap(CanStep::getPlacementDirection4)
                .orElseGet(super::getPlayerLookDirection);
    }

    @Override
    public Direction[] getPlacementDirections() {
        int i;
        Direction[] directions = Direction4.getEntityFacingOrder(this.getPlayer());
        if (this.canReplaceExisting) {
            return directions;
        }
        Direction direction = this.getSide();
        for (i = 0; i < directions.length && directions[i] != direction.getOpposite(); ++i) {
        }
        if (i > 0) {
            System.arraycopy(directions, 0, directions, 1, i);
            directions[0] = direction.getOpposite();
        }
        return directions;
    }

    @Override
    public Direction getPlayerFacing() {
        return CanStep.of(getPlayer())
                .flatMap(CanStep::getPlacementDirection4)
                .orElseGet(super::getPlayerFacing);
    }
}
