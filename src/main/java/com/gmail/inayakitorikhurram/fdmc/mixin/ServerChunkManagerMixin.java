package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.HasTicketManager;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin
        extends ChunkManager
        implements HasTicketManager {

    @Shadow
    @Final private ChunkTicketManager ticketManager;

    @Override
    public ChunkTicketManager getTicketManager() {
        return ticketManager;
    }

}
