package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface Direction4 extends StringIdentifiable {
    static Direction asDirection(Direction4 direction4) {
        return (Direction)(Object) direction4;
    }

    static Direction4 asDirection4(Direction direction) {
        return (Direction4)(Object) direction;
    }

    default Direction4 getOpposite4() {
        return asDirection4(getOpposite());
    }

    default int getOffsetW() {
        Vec3i vec3i = this.getVector();
        if (vec3i instanceof Vec4i<?>) {
            return ((Vec4i<?>) vec3i).getW();
        }
        return 0;
    }

    default Axis4 getAxis4() {
        return Axis4.asAxis4(getAxis());
    }

    // TODO: Mixin into default Directions so they all use Vec4i
    default Vec4i<?> getVector4() {
        Vec3i vec3i = this.getVector();
        if (vec3i instanceof Vec4i<?>) {
            return (Vec4i<?>) vec3i;
        }
        return Vec4i.newVec4i(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 0);
    }

    Vec3d getColor();

    //inherited from Direction
    Quaternionf getRotationQuaternion();
    //inherited from Direction
    int getId();
    //inherited from Direction
    int getHorizontal();
    //inherited from Direction
    Direction.AxisDirection getDirection();
    //inherited from Direction
    Direction getOpposite();
    //inherited from Direction
    Direction rotateClockwise(Direction.Axis axis);
    //inherited from Direction
    Direction rotateCounterclockwise(Direction.Axis axis);
    //inherited from Direction
    Direction rotateYClockwise();
    //inherited from Direction
    Direction rotateYCounterclockwise();
    //inherited from Direction
    int getOffsetX();
    //inherited from Direction
    int getOffsetY();
    //inherited from Direction
    int getOffsetZ();
    //inherited from Direction
    Vector3f getUnitVector();
    //inherited from Direction
    String getName();
    //inherited from Direction
    Direction.Axis getAxis();
    //inherited from Direction
    float asRotation();
    //inherited from Direction
    Vec3i getVector();
    //inherited from Direction
    boolean pointsTo(float yaw);

    interface Axis4 {
        static Direction.Axis asAxis(Axis4 axis4) {
            return (Direction.Axis)(Object) axis4;
        }

        static Axis4 asAxis4(Direction.Axis axis) {
            return (Axis4)(Object) axis;
        }

        default int choose(int x, int y, int z, int w) {
            if (getName().equals("w")) {
                return w;
            }
            return choose(x, y, z);
        }

        default double choose(double x, double y, double z, double w) {
            if (getName().equals("w")) {
                return w;
            }
            return choose(x, y, z);
        }

        //inherited from Direction.Axis
        String getName();
        //inherited from Direction.Axis
        boolean test(@Nullable Direction direction);
        //inherited from Direction.Axis
        int choose(int var1, int var2, int var3);
        //inherited from Direction.Axis
        double choose(double var1, double var3, double var5);
        //inherited from Direction.Axis
        boolean test(@Nullable Object object);
    }

    interface Type4{
        //TODO
    }

}
