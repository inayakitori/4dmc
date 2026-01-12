package com.gmail.inayakitorikhurram.fdmc.mixin.network.encoding;

import com.gmail.inayakitorikhurram.fdmc.math.RelativeVec4d;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.encoding.VelocityEncoding;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// TODO
@Mixin(VelocityEncoding.class)
public class VelocityEncodingMixin {
	@Overwrite
	public static Vec3d readVelocity(ByteBuf buf) {
		return RelativeVec4d.PACKET_CODEC.decode(buf);
	}

	@Overwrite
	public static void writeVelocity(ByteBuf buf, Vec3d velocity) {
        RelativeVec4d.PACKET_CODEC.encode(buf, velocity);
	}
}
