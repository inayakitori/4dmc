package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.ArrayUtils;
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

    static Direction[] getEntityFacingOrder(Entity entity) {
        Direction[] baseOrder = Direction.getEntityFacingOrder(entity);
        return CanStep.of(entity)
                .flatMap(CanStep::getPlacementDirection4)
                .map(direction -> ArrayUtils.add(ArrayUtils.addFirst(baseOrder, direction), direction.getOpposite()))
                .orElseGet(() -> ArrayUtils.addAll(baseOrder, Direction4Constants.KATA, Direction4Constants.ANA));
    }

    default Direction asDirection() {
        return (Direction)(Object) this;
    }

    default Direction4 getOpposite4() {
        return asDirection4(getOpposite());
    }

    default int getOffsetX4() {
        return this.getVector4().getX4();
    }

    default int getOffsetY4() {
        return this.getVector4().getY4();
    }

    default int getOffsetZ4() {
        return this.getVector4().getZ4();
    }

    default int getOffsetW4() {
        return this.getVector4().getW4();
    }

    default int getOffsetW() {
        return this.getVector4().getW();
    }

    default Axis4 getAxis4() {
        return Axis4.asAxis4(getAxis());
    }

    default Vec4i<?,?> getVector4() {
        return Vec4i.asVec4i(this.getVector());
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


    default Direction rotateClockwiseInto(Direction.Axis axis){
        return switch (((Axis4)(Object)this.getAxis()).asEnum()){
            case X -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> throw new IllegalArgumentException("both axis same for direction");
                case Y -> this.rotateZClockwise();
                case Z -> this.rotateYClockwise();
                case W -> this.rotateXYClockwise();
            };
            case Y -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateZClockwise();
                case Y -> throw new IllegalArgumentException("both axis same for direction");
                case Z -> this.rotateYClockwise();
                case W -> this.rotateZXClockwise();
            };
            case Z -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateYClockwise();
                case Y -> this.rotateXClockwise();
                case Z -> throw new IllegalArgumentException("both axis same for direction");
                case W -> this.rotateXYClockwise();
            };
            case W -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateYZClockwise();
                case Y -> this.rotateZXClockwise();
                case Z -> this.rotateXYClockwise();
                case W -> throw new IllegalArgumentException("both axis same for direction");
            };
        };
    }
    default Direction rotateCounterclockwiseInto(Direction.Axis axis){
        return switch (((Axis4)(Object)this.getAxis()).asEnum()){
            case X -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> throw new IllegalArgumentException("both axis same for direction");
                case Y -> this.rotateZCounterclockwise();
                case Z -> this.rotateYCounterclockwise();
                case W -> this.rotateXYCounterclockwise();
            };
            case Y -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateZCounterclockwise();
                case Y -> throw new IllegalArgumentException("both axis same for direction");
                case Z -> this.rotateYCounterclockwise();
                case W -> this.rotateZXCounterclockwise();
            };
            case Z -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateYCounterclockwise();
                case Y -> this.rotateXCounterclockwise();
                case Z -> throw new IllegalArgumentException("both axis same for direction");
                case W -> this.rotateXYCounterclockwise();
            };
            case W -> switch (((Axis4)(Object)axis).asEnum()){
                case X -> this.rotateYZCounterclockwise();
                case Y -> this.rotateZXCounterclockwise();
                case Z -> this.rotateXYCounterclockwise();
                case W -> throw new IllegalArgumentException("both axis same for direction");
            };
        };
    }
    
    //ROTATIONS
    default Direction rotateClockwise(Direction.Axis axis1, Direction.Axis axis2) {
        return switch (((Axis4)(Object)axis1).asEnum()){
            case X -> switch (((Axis4)(Object)axis2).asEnum()){
                case X -> throw new IllegalArgumentException("axis can't be the same");
                case Y -> this.rotateXYClockwise();
                case Z -> this.rotateZXClockwise();
                case W -> this.rotateXClockwise();
            };
            case Y ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateXYClockwise();
                case Y -> throw new IllegalArgumentException("axis can't be the same");
                case Z -> this.rotateYZClockwise();
                case W -> this.rotateYClockwise();
            };
            case Z ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateZXClockwise();
                case Y -> this.rotateYZClockwise();
                case Z -> throw new IllegalArgumentException("axis can't be the same");
                case W -> this.rotateZClockwise();
            };
            case W ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateXClockwise();
                case Y -> this.rotateYClockwise();
                case Z -> this.rotateZClockwise();
                case W -> throw new IllegalArgumentException("axis can't be the same");
            };
        };
    }
    default Direction rotateCounterclockwise(Direction.Axis axis1, Direction.Axis axis2) {
        return switch (((Axis4)(Object)axis1).asEnum()){
            case X -> switch (((Axis4)(Object)axis2).asEnum()){
                case X -> throw new IllegalArgumentException("axis can't be the same");
                case Y -> this.rotateXYCounterclockwise();
                case Z -> this.rotateZXCounterclockwise();
                case W -> this.rotateXCounterclockwise();
            };
            case Y ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateXYCounterclockwise();
                case Y -> throw new IllegalArgumentException("axis can't be the same");
                case Z -> this.rotateYZCounterclockwise();
                case W -> this.rotateYCounterclockwise();
            };
            case Z ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateZXClockwise();
                case Y -> this.rotateYZCounterclockwise();
                case Z -> throw new IllegalArgumentException("axis can't be the same");
                case W -> this.rotateZCounterclockwise();
            };
            case W ->  switch (((Axis4)(Object)axis2).asEnum()){
                case X -> this.rotateXCounterclockwise();
                case Y -> this.rotateYCounterclockwise();
                case Z -> this.rotateZCounterclockwise();
                case W -> throw new IllegalArgumentException("axis can't be the same");
            };
        };
    }
    Direction rotateXClockwise();
    default Direction rotateInYZClockwise() {return rotateXClockwise();}
    Direction rotateXCounterclockwise();
    default Direction rotateInYZCounterclockwise() {return rotateXCounterclockwise();}
    Direction rotateYClockwise() ;
    default Direction rotateInZXClockwise() {return rotateYClockwise();}
    Direction rotateYCounterclockwise();
    default Direction rotateInZXCounterclockwise() {return rotateYCounterclockwise();}
    Direction rotateZClockwise();
    default Direction rotateInXYClockwise() {return rotateZClockwise();}
    Direction rotateZCounterclockwise();
    default Direction rotateInXYCounterclockwise() {return rotateZCounterclockwise();}
    //ROTATIONS W
    Direction rotateYZClockwise();
    default Direction rotateInXWClockwise() {return rotateYZClockwise();}
    Direction rotateYZCounterclockwise();
    default Direction rotateInXWCounterclockwise() {return rotateYZCounterclockwise();}
    Direction rotateZXClockwise();
    default Direction rotateInYWClockwise() {return rotateZXClockwise();}
    Direction rotateZXCounterclockwise();
    default Direction rotateInYWCounterclockwise() {return rotateZXCounterclockwise();}
    Direction rotateXYClockwise();
    default Direction rotateInZWClockwise() {return rotateXYClockwise();}
    Direction rotateXYCounterclockwise();
    default Direction rotateInZWCounterclockwise() {return rotateXYCounterclockwise();}

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
