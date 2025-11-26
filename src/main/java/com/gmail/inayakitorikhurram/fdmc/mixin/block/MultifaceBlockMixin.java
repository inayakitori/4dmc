package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultifaceBlock.class)
public class MultifaceBlockMixin {
    @WrapMethod(method = "hasDirection")
    private static boolean fdmc$modifyDirections(BlockState state, Direction direction, Operation<Boolean> this$hasDirection){
        if(direction.getAxis() == Direction4Constants.Axis4Constants.W){
            return false;
        }

        return this$hasDirection.call(state, direction);
    }
}
