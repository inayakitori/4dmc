package com.gmail.inayakitorikhurram.fdmc.mixin.state.property;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.state.property.Direction4Property;
import com.gmail.inayakitorikhurram.fdmc.state.property.EnumProperty4;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;

@Mixin(Properties.class)
public abstract class PropertiesMixin {
    @Shadow @Final @Mutable
    public static final DirectionProperty HOPPER_FACING = DirectionProperty.of("facing", Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction != Direction.UP).toList());
    @Shadow @Final @Mutable
    public static final DirectionProperty HORIZONTAL_FACING = Direction4Property.of("facing", Direction4Constants.HORIZONTAL);
    @Shadow @Final @Mutable
    public static final DirectionProperty FACING = Direction4Property.of("facing", Direction4Constants.VALUES);
    @Shadow @Final @Mutable
    public static EnumProperty<Direction.Axis> HORIZONTAL_AXIS = EnumProperty4.of("axis", Direction.Axis.class, List.of(Direction.Axis.X, Direction.Axis.Z), List.of(Direction.Axis.X, Direction.Axis.Z, Direction4Constants.Axis4Constants.W));
}
