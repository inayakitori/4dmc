package com.gmail.inayakitorikhurram.fdmc.network.packet;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerPlacementC2SPacket(int stepDirection) implements CustomPayload {
    public static final Id<PlayerPlacementC2SPacket> ID = new Id<>(Identifier.of("fdmc", "player_placing"));
    public static final PacketCodec<RegistryByteBuf, PlayerPlacementC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, PlayerPlacementC2SPacket::stepDirection, PlayerPlacementC2SPacket::new);
    // should you need to send more data, add the appropriate record parameters and change your codec:
    // public static final PacketCodec<RegistryByteBuf, BlockHighlightPayload> CODEC = PacketCodec.tuple(
    //         BlockPos.PACKET_CODEC, BlockHighlightPayload::blockPos,
    //         PacketCodecs.INTEGER, BlockHighlightPayload::myInt,
    //         Uuids.PACKET_CODEC, BlockHighlightPayload::myUuid,
    //         BlockHighlightPayload::new
    // );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
