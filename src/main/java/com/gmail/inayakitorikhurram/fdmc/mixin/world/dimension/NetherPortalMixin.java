package com.gmail.inayakitorikhurram.fdmc.mixin.world.dimension;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(NetherPortal.class)
public abstract class NetherPortalMixin {

    @Mutable
    @Shadow @Final private Direction negativeDir;

    @Shadow @Final private Direction.Axis axis;

    @Inject(method = "getOrEmpty", at = @At("RETURN"), cancellable = true)
    private static void getOrEmptyUseW(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis axis, CallbackInfoReturnable<Optional<NetherPortal>> cir) {
        Optional<NetherPortal> optionalNetherPortal = cir.getReturnValue();
        if (optionalNetherPortal.isEmpty()) {
            cir.setReturnValue(Optional.of(new NetherPortal(world, pos, Direction4Constants.Axis4Constants.W)).filter(validator));
            cir.cancel();
        }
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/dimension/NetherPortal;negativeDir:Lnet/minecraft/util/math/Direction;"))
    private void negativeDirUseW(NetherPortal instance, Direction value) {
        this.negativeDir = this.axis == Direction4Constants.Axis4Constants.W ? Direction4Constants.KATA : value;
    }
}
