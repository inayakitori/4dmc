package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(Properties.class)
public class PropertiesMixin {
    @Shadow
    @Final
    public static final DirectionProperty HOPPER_FACING = DirectionProperty.of("facing", Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction != Direction.UP).toList());
}
