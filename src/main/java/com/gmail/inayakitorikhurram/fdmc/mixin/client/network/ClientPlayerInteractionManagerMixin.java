package com.gmail.inayakitorikhurram.fdmc.mixin.client.network;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(value = EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Redirect(
		method = "method_41933",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;I)Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;"
		)
	)
	PlayerInteractBlockC2SPacket fdmc$modifySentBlockInteractionPos(Hand hand, BlockHitResult blockHitResult, int sequence){
		Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
		return new PlayerInteractBlockC2SPacket(
			hand,
			camera == null ? blockHitResult : camera.getPerspective4().projectInverse(blockHitResult),
			sequence
		);
	}

	@Redirect(
		method = "method_41936",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;I)Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;"
		)
	)
	PlayerActionC2SPacket fdmc$modifySentStartDestroyBlockPosCreative(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction, int sequence){
		Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
		return new PlayerActionC2SPacket(
			action,
			camera == null ? pos : camera.getPerspective4().projectInverse(pos),
			camera == null ? direction : camera.getPerspective4().projectInverse(direction),
			sequence
		);
	}

	@Redirect(
		method = "attackBlock",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;"
		)
	)
	PlayerActionC2SPacket fdmc$modifySentAbortDestroyBlockPos(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction){
		Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
		return new PlayerActionC2SPacket(
			action,
			camera == null ? pos : camera.getPerspective4().projectInverse(pos),
			camera == null ? direction : camera.getPerspective4().projectInverse(direction)
		);
	}

	@Redirect(
		method = "method_41930",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;I)Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;"
		)
	)
	PlayerActionC2SPacket fdmc$modifySentStartDestroyBlockPosSurvival(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction, int sequence){
		Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
		return new PlayerActionC2SPacket(
			action,
			camera == null ? pos : camera.getPerspective4().projectInverse(pos),
			camera == null ? direction : camera.getPerspective4().projectInverse(direction),
			sequence
		);
	}

}
