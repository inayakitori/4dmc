package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Axis4Map;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumMap;
import java.util.Map;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newEnumMap(Ljava/util/Map;)Ljava/util/EnumMap;"))
    private static EnumMap<Direction.Axis, ?> fdmc$modifyEnumMap(Map<Direction.Axis,?> map, Operation<EnumMap<Direction.Axis, ?>> original){

        return Axis4Map.of(map);
    }
}
