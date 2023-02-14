package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface Direction4 extends StringIdentifiable {
    static Direction4 byId(int id) {
        return Direction4Constants.VALUES4[MathHelper.abs(id % Direction4Constants.VALUES4.length)];
    }

    static Direction4 asDirection4(Direction direction) {
        return (Direction4)(Object) direction;
    }

    default Direction asDirection() {
        return (Direction)(Object) this;
    }

    default Direction4 getOpposite4() {
        return asDirection4(getOpposite());
    }

    default int getOffsetW() {
        Vec3i vec3i = this.getVector();
        if (vec3i instanceof Vec4i<?,?>) {
            return ((Vec4i<?,?>) vec3i).getW();
        }
        return 0;
    }

    default Axis4 getAxis4() {
        return Axis4.asAxis4(getAxis());
    }

    // TODO: Mixin into default Directions so they all use Vec4i
    default Vec4i<?,?> getVector4() {
        Vec3i vec3i = this.getVector();
        if (vec3i instanceof Vec4i<?,?>) {
            return (Vec4i<?,?>) vec3i;
        }
        return Vec4i.newVec4i(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 0);
    }

    Direction4Enum asEnum();
    Vec3d getColor();
    Direction[] getParallel();
    Direction[] getPerpendicular();
    Direction[] getPerpendicularHorizontal();

    //inherited from Direction
    default Quaternionf getRotationQuaternion() {
        return this.asDirection().getRotationQuaternion();
    }
    //inherited from Direction
    default int getId() {
        return this.asDirection().getId();
    }
    //inherited from Direction
    default int getHorizontal() {
        return this.asDirection().getHorizontal();
    }
    //inherited from Direction
    default Direction.AxisDirection getDirection() {
        return this.asDirection().getDirection();
    }
    //inherited from Direction
    default Direction getOpposite() {
        return this.asDirection().getOpposite();
    }
    //inherited from Direction
    default Direction rotateClockwise(Direction.Axis axis) {
        return this.asDirection().rotateClockwise(axis);
    }
    //inherited from Direction
    default Direction rotateCounterclockwise(Direction.Axis axis) {
        return this.asDirection().rotateCounterclockwise(axis);
    }
    //inherited from Direction
    default Direction rotateYClockwise() {
        return this.asDirection().rotateYClockwise();
    }
    //inherited from Direction
    default Direction rotateYCounterclockwise() {
        return this.asDirection().rotateYCounterclockwise();
    }
    //inherited from Direction
    default int getOffsetX() {
        return this.asDirection().getOffsetX();
    }
    //inherited from Direction
    default int getOffsetY() {
        return this.asDirection().getOffsetY();
    }
    //inherited from Direction
    default int getOffsetZ() {
        return this.asDirection().getOffsetZ();
    }
    //inherited from Direction
    default Vector3f getUnitVector() {
        return this.asDirection().getUnitVector();
    }
    //inherited from Direction
    default String getName() {
        return this.asDirection().getName();
    }
    //inherited from Direction
    default Direction.Axis getAxis() {
        return this.asDirection().getAxis();
    }
    //inherited from Direction
    default float asRotation() {
        return this.asDirection().asRotation();
    }
    //inherited from Direction
    default Vec3i getVector() {
        return this.asDirection().getVector();
    }
    //inherited from Direction
    default boolean pointsTo(float yaw) {
        return this.asDirection().pointsTo(yaw);
    }

    interface Axis4 {
        default Direction.Axis asAxis() {
            return (Direction.Axis)(Object) this;
        }

        static Axis4 asAxis4(Direction.Axis axis) {
            return (Axis4)(Object) axis;
        }

        Direction4Enum.Axis4Enum asEnum();

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
        default String getName() {
            return this.asAxis().getName();
        }
        //inherited from Direction.Axis
        default boolean test(@Nullable Direction direction) {
            return this.asAxis().test(direction);
        }
        //inherited from Direction.Axis
        default int choose(int var1, int var2, int var3) {
            return this.asAxis().choose(var1, var2, var3);
        }
        //inherited from Direction.Axis
        default double choose(double var1, double var3, double var5) {
            return this.asAxis().choose(var1, var3, var5);
        }
    }

    interface Type4{
        //TODO
    }

}
