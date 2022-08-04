package com.gmail.inayakitorikhurram.fdmc.mixin;

import net.minecraft.server.world.ChunkTicketManager;

public interface HasTicketManager {
    ChunkTicketManager getTicketManager();
}
