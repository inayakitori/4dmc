package com.gmail.inayakitorikhurram.fdmc.mixin.client;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.gmail.inayakitorikhurram.fdmc.network.packet.Perspective4C2SPacket;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint.placeW;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    public abstract @Nullable Entity getCameraEntity();

    @Shadow
    @Final
    public GameOptions options;

    @WrapMethod(method = "handleInputEvents")
    private void handleInputEvents(Operation<Void> original){
        if (placeW.isPressed()) {
            Entity camera = this.getCameraEntity();
            FDMCConfig config = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig();

            boolean hasChangedPerspective4 = false;
            while (options.togglePerspectiveKey.wasPressed()) {
                if (camera == null || player == null) continue;
                // rotate 4d perspective
                // this is done via a mixin, otherwise keybinding flips vanilla perspective too
                Perspective4 perspective4 = ((Perspective4Access)player).getPerspective4();

                Direction fixedRenderDirection = switch(config.slice_rotation.fixed_direction) {
                    case FORWARD -> camera.getFacing();
                    case RIGHT -> camera.getHorizontalFacing().rotateYClockwise();
                };
                if (fixedRenderDirection.getAxis().isVertical()) {
                    camera.rotate(-90 * perspective4.renderW().getDirection().offset(), true, 0, true);
                }
                Direction4 facingLogicalDirection4 = switch (fixedRenderDirection) {
                    case EAST -> perspective4.renderX();
                    case WEST -> perspective4.renderX().getOpposite4();
                    case SOUTH -> perspective4.renderZ();
                    case NORTH -> perspective4.renderZ().getOpposite4();
                    case UP -> perspective4.renderW();
                    case DOWN -> perspective4.renderW().getOpposite4();
                };

                Perspective4 newPerspective = perspective4.rotateAround(
                    facingLogicalDirection4,
                    perspective4.renderY()
                );

                ((Perspective4Access) player).setPerspective4(newPerspective);
                hasChangedPerspective4 = true;
            }
            if (hasChangedPerspective4 && player != null) {
                ClientPlayNetworking.send(new Perspective4C2SPacket(((Perspective4Access) player).getPerspective4()));
            }
        }
        original.call();
    }

    @Redirect(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    BlockPos fdmc$itemPickLogicalBlockPos(BlockHitResult instance){
        Perspective4Access camera = (Perspective4Access) this.getCameraEntity();
        BlockPos renderPos = instance.getBlockPos();
        return camera == null ? renderPos : camera.getPerspective4().projectInverse(renderPos);
    }
}
