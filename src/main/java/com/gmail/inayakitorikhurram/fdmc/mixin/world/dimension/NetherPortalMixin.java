package com.gmail.inayakitorikhurram.fdmc.mixin.world.dimension;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.NetherPortal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherPortal.class)
public abstract class NetherPortalMixin {

    @Mutable
    @Shadow @Final private Direction negativeDir;

    @Shadow @Final private Direction.Axis axis;

    // TODO fix
//    @Inject(method = "getOrEmpty", at = @At("RETURN"), cancellable = true)
//    private static void getOrEmptyUseW(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis axis, CallbackInfoReturnable<Optional<NetherPortal>> cir) {
//        Optional<NetherPortal> optionalNetherPortal = cir.getReturnValue();
//        if (optionalNetherPortal.isEmpty()) {
//            cir.setReturnValue(Optional.of(NetherPortal.getOnAxis(world, pos, Direction4Constants.Axis4Constants.W)).filter(validator));
//            cir.cancel();
//        }
//    }

    @Redirect(method = "getOnAxis", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction;SOUTH:Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC))
    private static Direction negativeDirUseW(@Local(argsOnly = true) Direction.Axis axis) {
        return axis == Direction4Constants.Axis4Constants.W ? Direction4Constants.KATA : Direction.SOUTH;
    }
}
