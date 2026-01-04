package com.gmail.inayakitorikhurram.fdmc.mixin.client.input;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @Shadow
    @Final
    private GameOptions settings;

    @WrapOperation(method = "tick", at = @At(value = "NEW", target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;"))
    private PlayerInput fdmc$modifyForPerspective(
            boolean forward, boolean backward,
            boolean left, boolean right,
            boolean jump, boolean sneak,
            boolean sprint, Operation<PlayerInput> original){

        int stepInput = FDMCClientEntrypoint.getSteppingInput();

        if(!AutoConfig.getConfigHolder(FDMCConfig.class).get().slice_rotation.modify_player_inputs) {
            if(stepInput != 0) {
                ((CanStep) MinecraftClient.getInstance().player).scheduleStep(MathHelper.sign(stepInput), false);
                return original.call(false, false, false, false, jump, sneak, sprint);
            } else {
                return original.call(forward, backward, left, right, jump, sneak, sprint);
            }
        }

        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dz = (forward ? 1 : 0) - (backward ? 1 : 0);

        // y is forward --> z is forward
        Vec3d inputVec = new Vec3d(dx, 0, dz);
        Perspective4 perspective4 = ((GameOptions4)this.settings).getPerspective4();
        float cameraYaw = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraYaw();
        AxisAngle4f cameraRotation = new AxisAngle4f((float) Math.toRadians(cameraYaw), new Vector3f(0, 1, 0));
        Vector3f cameraSpaceVec = cameraRotation.transform(inputVec.toVector3f());
        Vec4d cameraSpaceInput = new Vec4d(cameraSpaceVec.x, cameraSpaceVec.y, cameraSpaceVec.z, stepInput);
        Vec4d wPerspectiveRotated = perspective4.project(cameraSpaceInput);
        double mappedStepInput = wPerspectiveRotated.w;
        AxisAngle4f cameraRotationInverse = new AxisAngle4f((float) -Math.toRadians(cameraYaw), new Vector3f(0, 1, 0));
        Vector3f mappedInput = cameraRotationInverse.transform(new Vector3f(
                (float) wPerspectiveRotated.x, (float) wPerspectiveRotated.y,
                (float) wPerspectiveRotated.z));

        if(Math.abs(mappedStepInput) > 0.75f) {
            ((CanStep) MinecraftClient.getInstance().player).scheduleStep(MathHelper.sign(mappedStepInput), false);
            return original.call(false, false, false, false, jump, sneak, sprint);
        }

        int newDx = Math.abs(mappedInput.x) > 0.5f ? MathHelper.sign(mappedInput.x) : 0;
        int newDz = Math.abs(mappedInput.z) > 0.5f ? MathHelper.sign(mappedInput.z) : 0;
        return original.call(newDz > 0, newDz < 0, newDx < 0, newDx > 0, jump, sneak, sprint);
    }

}
