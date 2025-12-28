package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(FuzzyPositions.class)
public class FuzzyPositionsMixin {
    // This makes it so that, when selecting a fuzzy target, the mobs will
    @WrapMethod(method = "localFuzz(Lnet/minecraft/util/math/random/Random;II)Lnet/minecraft/util/math/BlockPos;")
    private static BlockPos fdmc$fuzzW(Random random, int horizontalRange, int verticalRange, Operation<BlockPos> original){
        BlockPos pos1 = original.call(random, horizontalRange, verticalRange);
        if(pos1 == null) return null;
        int dw = random.nextInt(3)-1;
        BlockPos newPos = pos1.offset(Direction4Constants.ANA, dw);
        //FDMCConstants.LOGGER.info("New pos set (dw={}): {}", dw, newPos);
        return newPos;
    }
    // This makes it so that, when selecting a fuzzy target, the mobs will
    @WrapMethod(method = "localFuzz(Lnet/minecraft/util/math/random/Random;IIIDDD)Lnet/minecraft/util/math/BlockPos;")
    private static BlockPos fdmc$fuzzWScoped(Random random, int horizontalRange, int verticalRange, int startHeight, double directionX, double directionZ, double angleRange, Operation<BlockPos> original){
        BlockPos pos1 = original.call(random, horizontalRange, verticalRange, startHeight, directionX, directionZ, angleRange);
        if(pos1 == null) return null;
        int dw = random.nextInt(3)-1;
        BlockPos newPos = pos1.offset(Direction4Constants.ANA, dw);
        //FDMCConstants.LOGGER.info("New pos set (dw={}): {}", dw, newPos);
        return newPos;
    }

//    @WrapMethod(method = "towardTarget")
//    private static BlockPos fdmc$towardsTarget(PathAwareEntity entity, int horizontalRange, Random random, BlockPos fuzz, Operation<BlockPos> original){
//        BlockPos4 fuzz4 = BlockPos4.of(fuzz);
//        BlockPos4 inEntitySliceFuzz = BlockPos4.newBlockPos4(fuzz4.getX4(), fuzz4.getY(), fuzz4.getZ(), BlockPos4.of(entity.blockPos).getW4());
//        int fuzzDW = fuzz4.getW4() - inEntitySliceFuzz.getW4();
//        if(fuzzDW != 0) {
//            FDMCConstants.LOGGER.info("Fuzzy target is outside of slice (dw={})", fuzzDW);
//        }
//        BlockPos flatFuzz = inEntitySliceFuzz.asBlockPos();
//        return original.call(entity, horizontalRange, random, flatFuzz);
//    }

}
