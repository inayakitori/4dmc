package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.Direction;

import java.util.Optional;


//first axis is the left/right axis, second is the kata/ana axis
public record DoubleChestType(ChestType axis1, ChestType axis2) {
    public static final DoubleChestType SINGLE = new DoubleChestType(ChestType.SINGLE, ChestType.SINGLE);

    public static DoubleChestType of(ChestAdjacencyAxis axis, ChestType chestType){
        return switch (axis){
            case LEFTRIGHT -> new DoubleChestType(chestType, ChestType.SINGLE);
            case KATAANA -> new DoubleChestType(ChestType.SINGLE, chestType);
        };
    }

    public ChestType get(ChestAdjacencyAxis axis){
        return switch (axis){
            case LEFTRIGHT -> axis1;
            case KATAANA -> axis2;
        };
    }

    //not a proper union, only works if only one of each is defined
    public static Optional<DoubleChestType> union(DoubleChestType chest1, DoubleChestType chest2){
        if(chest1.axis1 == ChestType.SINGLE && chest2.axis2 == ChestType.SINGLE){
            return Optional.of(new DoubleChestType(chest2.axis1, chest1.axis2));
        } else if(chest2.axis1 == ChestType.SINGLE && chest1.axis2 == ChestType.SINGLE){
            return Optional.of(new DoubleChestType(chest1.axis1, chest2.axis2));
        } else{
            return Optional.empty();
        }
    }

    public boolean isSingle(){
        return axis1 == ChestType.SINGLE && axis2 == ChestType.SINGLE;
    }

    public boolean isDouble(){
        return
                axis1 == ChestType.SINGLE && axis2 != ChestType.SINGLE ||
                axis1 != ChestType.SINGLE && axis2 == ChestType.SINGLE;
    }

    public boolean isDoubleOn(ChestAdjacencyAxis axis){
        return
                get(axis) != ChestType.SINGLE && get(axis.cycle()) == ChestType.SINGLE;
    }


    public boolean isQuad(){
        return axis1 != ChestType.SINGLE && axis2 != ChestType.SINGLE;
    }
}

