package com.gmail.inayakitorikhurram.fdmc.math;


import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Consumer;

//for extending already existing directions
public enum OptionalDirection4 implements StringIdentifiable {

    ANA(+1, Direction4.ANA, Direction4.ANA.getName()),
    NONE(0, null, "none"),
    KATA(-1, Direction4.KATA, Direction4.KATA.getName());

    private final int id;
    private final Direction4 actualDirection4;
    private final String name;

    OptionalDirection4(int id, Direction4 actualDirection4, String name) {
        this.id = id;
        this.actualDirection4 = actualDirection4;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public Optional<Direction4> get(){
        return Optional.ofNullable(this.actualDirection4);
    }

    public Direction4 toDir4(Direction dir3Fallback){
        final Direction4[] dir4 = new Direction4[1];//the array wrapping allows access for some reason?
        this.ifPresentElse(direction4 -> {
            dir4[0] = direction4;
        }, () -> {
            dir4[0] = Direction4.fromDirection3(dir3Fallback);
        });
        return dir4[0];
    }

    public void ifPresentElse(Consumer<Direction4> presentAction, Runnable notPresentAction){
        if(this != NONE){
            presentAction.accept(this.actualDirection4);
        } else{
            notPresentAction.run();
        }
    }

    public void ifPresent(Consumer<Direction4> action) {
        if (this != NONE) {
            action.accept(this.actualDirection4);
        }
    }

    public void ifNotPresent(Runnable action) {
        if (this == NONE) {
            action.run();
        }
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static OptionalDirection4 fromId(int id){
        return switch (id){
            case  +1 -> ANA;
            case   0 -> NONE;
            case  -1 -> KATA;
            default -> throw new IllegalArgumentException("optional id must be between -1 and 1");
        };
    }

    public OptionalDirection4 getOpposite() {
        switch (this){
            case ANA -> {
                return KATA;
            }
            case KATA -> {
                return ANA;
            }
            default -> {
                return NONE;
            }
        }
    }
}
