package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.stream.IntStreams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants.*;

class Perspective4Test {

    private static final DynamicOps[] CODEC_OPS = new DynamicOps[]{JsonOps.INSTANCE, JsonOps.COMPRESSED, NbtOps.INSTANCE, JavaOps.INSTANCE};
    private static ImmutableList<Perspective4> VALUES;
    private static ImmutableList<Pair<Direction4, Direction4>> ROTATIONS;
    
    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        VALUES = ImmutableList.copyOf(
                    // for every permutation of Directions
                Collections2.permutations(List.of(
                        Direction4Constants.EAST,
                        Direction4Constants.UP,
                        Direction4Constants.SOUTH,
                        Direction4Constants.ANA
                    ))
                    .stream().flatMap(permutedDirs ->
                            //for every combination of AxisDirection
                        IntStreams.range(16).mapToObj(i ->
                            //create a list of Directions w appropriate sign
                            Perspective4.fromDirectionList(IntStreams.range(4).mapToObj(axisIndex -> {
                                Direction dir = permutedDirs.get(axisIndex);
                                if(((i >> axisIndex) & 1) == 1){
                                    dir = dir.getOpposite();
                                }
                                return dir;
                            }).toList())
                        )
                    ).toList()
            );
        ROTATIONS = ImmutableList.copyOf(
            Arrays.stream(VALUES4).flatMap(dir1 ->
                Arrays.stream(VALUES4)
                    .filter(dir2 -> dir1.getAxis() != dir2.getAxis())
                    .flatMap(dir2 -> Stream.of(
                            new Pair<>(dir1, dir2),
                            new Pair<>(dir2, dir1)
                    ))
                ).toList()
            );
    }


    Perspective4 applyRotation(Perspective4 perspective4, Pair<Direction4, Direction4> rotation){
        return perspective4.rotateAround(rotation.getFirst(), rotation.getSecond());
    }

    Perspective4 applyRotation(Perspective4 perspective4, Pair<Direction4, Direction4> rotation, int times){
        for(int i = 0; i < times; i++) {
            perspective4 = perspective4.rotateAround(rotation.getFirst(), rotation.getSecond());
        }
        return perspective4;
    }

    Perspective4 applyRotations(Perspective4 perspective4, List<Pair<Direction4, Direction4>> rotations, int times){
        for(int i = 0; i < times; i++) {
            for(Pair<Direction4, Direction4> rotation : rotations) {
                applyRotation(perspective4, rotation);
            }
        }
        return perspective4;
    }

    @Test
    void rotateAround(){
        //some simple tests
        Perspective4 perspective4 = Perspective4.DEFAULT.rotateAround(UP4, EAST4);
        Assertions.assertEquals(new Perspective4(EAST4, UP4, ANA4, NORTH4), perspective4);
        perspective4 = perspective4.rotateAround(UP4, ANA4);
        Assertions.assertEquals(new Perspective4(SOUTH4, UP4, ANA4, EAST4), perspective4);
    }

    @Test
    void rotationIdentity1(){
        //Rotating around 4 times is the same as doing nothing
        for(Perspective4 perspective4 : VALUES) {
            for(Pair<Direction4, Direction4> rotation : ROTATIONS) {
                Assertions.assertEquals(
                        perspective4,
                        applyRotation(perspective4, rotation, 4)
                );
            }
        }
    }
    
    @Test
    void rotationIdentity2(){
        //repeating two rotations over and over will return to the original state after three repeats

        for(Perspective4 perspective4 : randomSubList(VALUES, 50)) {
            for(Pair<Direction4, Direction4> rot1 : randomSubList(ROTATIONS, 30)) {
                for(Pair<Direction4, Direction4> rot2 : randomSubList(ROTATIONS, 30)) {
                    Assertions.assertEquals(
                            perspective4,
                            applyRotations(perspective4, List.of(rot1, rot2), 3)
                    );
                }
            }
        }
    }

    @Test
    void encodeDecodeCodec(){
        for(Perspective4 perspective4 : VALUES){
            for(DynamicOps ops : CODEC_OPS){
                encodeDecodeWithOps(perspective4, ops);
            }
        }
    }

    <T> void encodeDecodeWithOps(Perspective4 perspective4, DynamicOps<T> ops){
        T encode = (Perspective4.CODEC.encodeStart(ops, perspective4).getOrThrow());
        Pair<Perspective4, T> decode = Perspective4.CODEC.decode(ops, encode).getOrThrow();
        Assertions.assertEquals(decode.getFirst(), perspective4);
    }

    // Encode everything into a single packet buffer and then also decode. Should all be equal
    @Test
    void encodeDecodePacketCodec(){
        PacketByteBuf buffer = PacketByteBufs.create();
        for(Perspective4 perspective4 : VALUES){
            Perspective4.PACKET_CODEC.encode(buffer, perspective4);
        }
        for(Perspective4 perspective4 : VALUES){
            Perspective4 encodeDecode = Perspective4.PACKET_CODEC.decode(buffer);
            Assertions.assertEquals(perspective4, encodeDecode);
        }
    }


    @Test
    void projectThenProjectInverse() {
        for(Perspective4 perspective4 : VALUES){
            Random random = new Random();
            double maxExponent = 32;
            int n = 500;
            for(int i = 0; i < n ; i++){
                Vec4d initial = randomVec4d(random, (i+1) * maxExponent /n);
                Vec4d projectThenProjectInverse = perspective4.projectInverse(perspective4.project(initial));
                Assertions.assertEquals(initial, projectThenProjectInverse);
            }
        }
    }

    @Test
    void projectInverseThenProject() {
        for(Perspective4 perspective4 : VALUES){
            Random random = new Random();
            double maxExponent = 32;
            int n = 500;
            for(int i = 0; i < n ; i++){
                Vec4d initial = randomVec4d(random, (i+1) * maxExponent /n);
                Vec4d projectInverseThenProject = perspective4.project(perspective4.projectInverse(initial));
                Assertions.assertEquals(initial, projectInverseThenProject);
            }
        }
    }

    private Vec4d randomVec4d(Random random, double expScale){
        double scale = Math.pow(2, random.nextDouble(-expScale,expScale));
        return new Vec4d(
                random.nextDouble(-1.0f, 1.0f),
                random.nextDouble(-1.0f, 1.0f),
                random.nextDouble(-1.0f, 1.0f),
                random.nextDouble(-1.0f, 1.0f)
        ).multiply(scale);
    }

    private <E> List<E> randomSubList(List<E> original, int size){
        return new Random()
                .ints(size, 0, size)
                .mapToObj(original::get)
                .toList();
    }

}