package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @WrapOperation(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(DDD)D"))
    private double fdmc$modifiedSquaredDistance(Vec3d cameraPos, double x, double y, double z, Operation<Double> cameraPos$squaredDistanceTo){
        return Vec4d.of(cameraPos).squaredDistanceTo(new Vec4d(x, y, z));
    }

    @WrapOperation(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V",
    at = @At(value = "NEW", target = "(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFLnet/minecraft/util/math/random/Random;DDD)Lnet/minecraft/client/sound/PositionedSoundInstance;"))
    private PositionedSoundInstance fdmc$modifyPositionedSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, double x, double y, double z, Operation<PositionedSoundInstance> init){
        assert MinecraftClient.getInstance().player != null;
        double playerX3 = MinecraftClient.getInstance().player.getX();
        // makes sure the sound x is in the same slice as the player but retains the X location within a slice
        double[] playerXW = FDMCMath.splitX3(playerX3);
        double playerX = playerXW[0];
        double playerW = playerXW[1];
        double[] soundXW = FDMCMath.splitX3(x);
        double soundX = soundXW[0];
        double soundW = soundXW[1];

        double newSoundX = soundX + FDMCMath.getOffsetX(playerW);
        float dw = (float) (soundW - playerW);

        // This kinda gives a nice falloff
        float volumeModifier = (float) Math.max( (1 - Math.pow(Math.abs(dw)/3.5, 1.5)), 0);

        //increase the pitch diff for at least one slice out
        int tones = (int) dw;
        if(Math.abs(dw) > 1.5){
            tones++;
        } else if (Math.abs(dw) < -1.5) {
            tones--;

        }
        float pitchModifier = (float) Math.pow(2f, tones / 4f);

        return init.call(sound, category, volume * volumeModifier, pitch * pitchModifier, random, newSoundX, y, z);
    }

}
