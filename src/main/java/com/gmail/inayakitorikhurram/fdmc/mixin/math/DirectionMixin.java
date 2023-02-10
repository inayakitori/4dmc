package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(Direction.class)
public abstract class DirectionMixin {

    @Shadow
    @Final
    @Mutable
    private static Direction[] field_11037;

    private static final Direction KATA = fdmc$addDirection("KATA", 6, 7, 4, "kata", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, Vec4i.newVec4i(0, 0, 0, -1));
    private static final Direction ANA = fdmc$addDirection("ana", 7, 6, 5, "ana", Direction.AxisDirection.POSITIVE, Direction.Axis.X, Vec4i.newVec4i(0, 0, 0, 1));

    @Invoker("<init>")
    public static Direction fdmc$invokeInit(String internalName, int internalId, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        throw new AssertionError();
    }

    private static Direction fdmc$addDirection(String internamName, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        ArrayList<Direction> values = new ArrayList<Direction>(Arrays.asList(field_11037));
        Direction dir = fdmc$invokeInit(internamName, values.get(values.size() - 1).ordinal() + 1, id, idOpposite, idHorizontal, name, direction, axis, vector);
        values.add(dir);
        field_11037 = values.toArray(new Direction[0]);
        System.out.println(name);
        return dir;
    }

    @Mixin(Direction.Axis.class)
    public static class AxisMixin {

    }
}
