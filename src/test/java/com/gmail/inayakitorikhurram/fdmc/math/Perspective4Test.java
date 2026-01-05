package com.gmail.inayakitorikhurram.fdmc.math;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

class Perspective4Test {

    public static final DynamicOps[] CODEC_OPS = new DynamicOps[]{JsonOps.INSTANCE, JsonOps.COMPRESSED, NbtOps.INSTANCE, JavaOps.INSTANCE};

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void encodeDecodeCodec(){
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        for(Perspective4 perspective4 : Perspective4.VALUES){
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
        for(Perspective4 perspective4 : Perspective4.VALUES){
            Perspective4.PACKET_CODEC.encode(buffer, perspective4);
        }
        for(Perspective4 perspective4 : Perspective4.VALUES){
            Perspective4 encodeDecode = Perspective4.PACKET_CODEC.decode(buffer);
            Assertions.assertEquals(perspective4, encodeDecode);
        }
    }


    @Test
    void projectThenProjectInverse() {
        for(Perspective4 perspective4 : Perspective4.VALUES){
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
        for(Perspective4 perspective4 : Perspective4.VALUES){
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

}