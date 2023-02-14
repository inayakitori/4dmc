package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.state.property.Direction4Property;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(Properties.class)
public class PropertiesMixin {
    @Shadow @Final @Mutable
    public static final DirectionProperty HOPPER_FACING = DirectionProperty.of("facing", Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction != Direction.UP).toList());

    @Shadow @Final @Mutable
    public static final DirectionProperty HORIZONTAL_FACING = Direction4Property.of("facing", Direction4Constants.HORIZONTAL);
}
