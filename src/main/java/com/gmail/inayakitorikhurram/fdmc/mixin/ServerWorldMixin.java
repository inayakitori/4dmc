package com.gmail.inayakitorikhurram.fdmc.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
        extends WorldMixin
        implements StructureWorldAccess {

    @ModifyConstant(method = "setSpawnPos", constant = @Constant(intValue = 11))
    private int injectedStartRegionRange(int value) {
        return 5;
    }

}
