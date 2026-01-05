package com.gmail.inayakitorikhurram.fdmc.mixin.client.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.IDebugHudMixin;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements DataTracked {
    @Shadow
    private World world;

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (world.isClient() && Perspective4.TRACKED_DATA.equals(data)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (((Entity)(Object) this).equals(client.getCameraEntity())) {
                IDebugHudMixin debugHud = (IDebugHudMixin) client.getDebugHud();
                debugHud.fdmc$refreshDebugCrosshairBuffer(((Perspective4Access) this).getPerspective4());
            }
        }
    }
}
