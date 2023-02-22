package com.gmail.inayakitorikhurram.fdmc.block.enums;

public enum ChestType4Enum {
    SINGLE(0),
    LEFT(1),
    RIGHT(2),
    KATA(3),
    ANA(4);

    private final int id;

    ChestType4Enum(int id) {
        this.id = id;
    }

    public static ChestType4Enum byId(int id){
        for(ChestType4Enum chestType : values()){
            if(chestType.id == id) return chestType;
        }
        return null;
    }

    public int getId() {
        return id;
    }

}
