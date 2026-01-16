package com.gmail.inayakitorikhurram.fdmc.mixin.world.poi;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.stream.Stream;

@Mixin(PointOfInterestStorage.class)
public class PointOfInterestStorageMixin {

    @WrapOperation(method = "getInSquare", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkPos;stream(Lnet/minecraft/util/math/ChunkPos;I)Ljava/util/stream/Stream;"))
    private Stream<ChunkPos> fdmc$extendStream(ChunkPos center, int radius, Operation<Stream<ChunkPos>> original){
        return ChunkPos4.extendedCircularStream(center, radius);
    }


//    private static void fdmc$modifiedCircularStream(Args args, @Local(argsOnly = true) int radius){
//        int dw = FDMCMath.getOffsetX(radius);
//        ChunkPos4 start = new ChunkPos4((ChunkPos) args.get(0));
//        ChunkPos4 end = new ChunkPos4((ChunkPos) args.get(1));
//        args.set(0, new ChunkPos4(start.x, start.z, start.w - radius).toPos3());
//        args.set(1, new ChunkPos4(end.x, end.z, end.w + radius).toPos3());
//    }

    @Inject(method = "method_30335", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterest;getPos()Lnet/minecraft/util/math/BlockPos;", shift = At.Shift.AFTER), cancellable = true)
    private static void fdmc$modifyManhattanDistance(BlockPos pos, int radius, PointOfInterest poi, CallbackInfoReturnable<Boolean> cir){
        BlockPos4 pos4 = BlockPos4.of(pos);

        BlockPos4 poi4 = BlockPos4.of(poi.getPos());

        int dx = MathHelper.abs(pos4.getX4() - poi4.getX4());
        int dz = MathHelper.abs(pos4.getZ4() - poi4.getZ4());
        int dw = MathHelper.abs(pos4.getW4() - poi4.getW4());

        cir.setReturnValue(dx <= radius && dz <= radius && dw <= (radius >> 4));
        cir.cancel();

    }
}
