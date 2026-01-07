package com.gmail.inayakitorikhurram.fdmc.network.packet;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record Perspective4C2SPacket(Perspective4 perspective) implements CustomPayload {
    public static final Id<Perspective4C2SPacket> ID = new Id<>(Identifier.of("fdmc", "perspective"));
    public static final PacketCodec<RegistryByteBuf, Perspective4C2SPacket> CODEC =
            PacketCodec.tuple(
                    Perspective4.PACKET_CODEC,
                    Perspective4C2SPacket::perspective,
                    Perspective4C2SPacket::new
                    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
