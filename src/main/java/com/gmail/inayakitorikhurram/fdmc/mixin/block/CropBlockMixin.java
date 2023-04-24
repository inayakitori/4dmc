package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public class CropBlockMixin {



    //There are way too many iterators and stuff to actually inject into this individually, so we're just gonna do this instead
    @Inject(method = "getAvailableMoisture", at = @At("HEAD"), cancellable = true)
    private static void getAvailableMoisture(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float moisture = 1.0F;
        BlockPos4<?,?> below = (BlockPos4<?,?>) pos.down();

        for(int dx = -1; dx <= 1; ++dx) {
            for(int dz = -1; dz <= 1; ++dz) {
                for(int  dw = -1; dw <= 1; dw++) {
                    float moistureFromAdjacentFarmland = 0.0F;
                    BlockState adjacentFarmland = world.getBlockState((BlockPos) below.add4(dx, 0, dz, dw));
                    if (adjacentFarmland.isOf(Blocks.FARMLAND)) {
                        moistureFromAdjacentFarmland = 1.0F;
                        if (adjacentFarmland.get(FarmlandBlock.MOISTURE) > 0) {
                            moistureFromAdjacentFarmland = 3.0F;
                        }
                    }

                    if (dx != 0 || dz != 0 || dw != 0) {
                        //is adjacent, should scale down by a factor of the new number of neighbouring blocks
                        moistureFromAdjacentFarmland /= 4.0F;
                        moistureFromAdjacentFarmland *= 8.0f / 26.0f; // = (3^2 - 1) / (3^3 - 1)
                    }

                    moisture += moistureFromAdjacentFarmland;
                }
            }
        }


        //super cursed but basically allows us to break whenever we need to
        halving_loop: {

            //1D diagonals (adjacent)
            BlockPos east = pos.east();
            BlockPos west = pos.west();
            boolean adjacentX = world.getBlockState(east).isOf(block) || world.getBlockState(west).isOf(block);

            BlockPos north = pos.north();
            BlockPos south = pos.south();
            boolean adjacentZ = world.getBlockState(north).isOf(block) || world.getBlockState(south).isOf(block);


            BlockPos kata = (BlockPos)((BlockPos4<?, ?>)pos).kata4();
            BlockPos ana = (BlockPos)((BlockPos4<?, ?>)pos).ana4();
            boolean adjacentW = world.getBlockState(kata).isOf(block) || world.getBlockState(ana).isOf(block);

            //if it's adjacent on two or three, should be halved
            if ( adjacentX ? (adjacentZ || adjacentW) : (adjacentZ && adjacentW)) break halving_loop;

            //2D and 3D diagonals
            BlockPos4.Mutable4 pos4mut = BlockPos4.Mutable4.asMutable4(pos.mutableCopy());

            for(int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    for (int dw = -1; dw <= 1; dw++) {
                        //don't do self
                        if (dx == 0 && dz == 0 && dw == 0) continue;

                        //don't do 1D diagonals (adjacent)
                        if(
                            (dx != 0 && dz == 0 && dw == 0) ||
                            (dx == 0 && dz != 0 && dw == 0) ||
                            (dx == 0 && dz == 0 && dw != 0)
                        ) continue;

                        pos4mut.set(pos);
                        pos4mut.move4(dx, 0, dz, dw);

                        //if the crop is the same, cry and end
                        if(world.getBlockState((BlockPos)pos4mut).isOf(block)) {
                            break halving_loop;
                        }

                    }
                }
            }


            //if at least on side of a 1D diagonal is only the same once per axis and no 2D nor 3D diagonal crops are the same then have normal moisture level
            cir.setReturnValue(moisture);
            cir.cancel();
            return;
        }
        //otherwise half and return
        cir.setReturnValue(moisture / 2.0f);
        cir.cancel();
    }


}
