package com.gmail.inayakitorikhurram.fdmc.mixin.server.dedicated;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerPropertiesHandler.class)
public class ServerPropertiesHandlerMixin {

    // I hate targeting constants and WrapOperation is more reliable don't @ me
    @ModifyArg(method = "<init>",
    slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=view-distance")),
    at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler;intAccessor(Ljava/lang/String;I)Lnet/minecraft/server/dedicated/AbstractPropertiesHandler$PropertyAccessor;", ordinal = 0),
    index = 1)
    private int modifyInitialViewDistance(int fallback){
        return FDMCConstants.INITIAL_VIEW_DISTANCE;
    }
}
