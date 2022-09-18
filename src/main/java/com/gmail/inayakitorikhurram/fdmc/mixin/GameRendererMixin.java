package com.gmail.inayakitorikhurram.fdmc.mixin;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.gmail.inayakitorikhurram.fdmc.FDMCMainEntrypoint.LOGGER;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
        implements SynchronousResourceReloader,
        AutoCloseable  {

    @Redirect(
            method = "updateTargetedEntity(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 0
            )
    )
    private HitResult init(Entity instance, double maxDistance, float tickDelta, boolean includeFluids){
        HitResult initialResult = instance.raycast(maxDistance, tickDelta, includeFluids);
        //TODO check if actually trying to hit kata ana
        if(initialResult.getType() == HitResult.Type.ENTITY) return initialResult;


        //now we know it's not a wack wack
        Vec3d playerPos = instance.getPos();
        BlockPos blockPos = instance.getBlockPos().add(FDMCConstants.STEP_DISTANCE, -1, 0);

        instance.sendMessage(Text.of(
                "Player at {" + instance.getPos() + "} is attempting to break block at {" + blockPos + "}, currently on client side"
        ));


        return new BlockHitResult(playerPos, Direction.UP, blockPos, false);

    }


}
