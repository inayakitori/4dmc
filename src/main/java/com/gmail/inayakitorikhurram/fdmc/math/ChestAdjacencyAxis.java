package com.gmail.inayakitorikhurram.fdmc.math;

public enum ChestAdjacencyAxis {
    LEFTRIGHT,
    KATAANA;

    public ChestAdjacencyAxis cycle(){
        return switch (this){
            case LEFTRIGHT -> KATAANA;
            case KATAANA -> LEFTRIGHT;
        };
    }

}
