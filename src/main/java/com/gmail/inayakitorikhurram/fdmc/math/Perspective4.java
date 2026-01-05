package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public record Perspective4 (
	Direction4 renderX,
	Direction4 renderY,
	Direction4 renderZ,
	Direction4 renderW
) {
	public static final TrackedDataHandler<Perspective4> TRACKED_DATA_HANDLER = TrackedDataHandler.create(Perspective4.PACKET_CODEC);
	public static final TrackedData<Perspective4> TRACKED_DATA = DataTracker.registerData(Entity.class, TRACKED_DATA_HANDLER);

    public static final Perspective4 DEFAULT = new Perspective4(
		Direction4Constants.EAST4,
	    Direction4Constants.UP4,
	    Direction4Constants.SOUTH4,
	    Direction4Constants.ANA4
    );

    public static Perspective4 fromDirectionList(List<Direction> dirs){
        assert(dirs.size() == 4);
        return Perspective4.fromDirections(dirs.get(0), dirs.get(1), dirs.get(2), dirs.get(3));
    }

    public static Perspective4 fromDirections(
            Direction renderX,
            Direction renderY,
            Direction renderZ,
            Direction renderW){
        return new Perspective4(
                Direction4.asDirection4(renderX),
                Direction4.asDirection4(renderY),
                Direction4.asDirection4(renderZ),
                Direction4.asDirection4(renderW)
        );
    }

    public static final Codec<Perspective4> CODEC = Direction.CODEC.listOf().comapFlatMap(
            dirs -> Util.decodeFixedLengthList(dirs, 4).map(
                    Perspective4::fromDirectionList
            ),
            perspective4 -> List.of(
                    perspective4.renderX().asDirection(),
                    perspective4.renderY().asDirection(),
                    perspective4.renderZ().asDirection(),
                    perspective4.renderW().asDirection()
            )
    );

    public static final PacketCodec<ByteBuf, Perspective4> PACKET_CODEC = new PacketCodec<>() {

        @Override
        public Perspective4 decode(ByteBuf byteBuf) {
            Direction renderX = Direction.PACKET_CODEC.decode(byteBuf);
            Direction renderY = Direction.PACKET_CODEC.decode(byteBuf);
            Direction renderZ = Direction.PACKET_CODEC.decode(byteBuf);
            Direction renderW = Direction.PACKET_CODEC.decode(byteBuf);
            return Perspective4.fromDirections(renderX, renderY, renderZ, renderW);
        }

        @Override
        public void encode(ByteBuf byteBuf, Perspective4 p4) {
            Direction.PACKET_CODEC.encode(byteBuf, p4.renderX.asDirection());
            Direction.PACKET_CODEC.encode(byteBuf, p4.renderY.asDirection());
            Direction.PACKET_CODEC.encode(byteBuf, p4.renderZ.asDirection());
            Direction.PACKET_CODEC.encode(byteBuf, p4.renderW.asDirection());
        }

    };


    @Override
	public @NotNull String toString() {
		return
			  "X: " + renderX.getDirection().name() + " " + renderX.getAxis().id +
			", Y: " + renderY.getDirection().name() + " " + renderY.getAxis().id +
			", Z: " + renderZ.getDirection().name() + " " + renderZ.getAxis().id +
			"; W: " + renderW.getDirection().name() + " " + renderW.getAxis().id ;
	}

    public String toShortString() {
        return "(x,y,z,w) = (" +
                shorthandDirectionText(renderX) + "," +
                shorthandDirectionText(renderY) + "," +
                shorthandDirectionText(renderZ) + "," +
                shorthandDirectionText(renderW) + ")";
    }

    private static String shorthandDirectionText(Direction4 dir){
        return dir.getAxis().id + directionSymbol(dir.getDirection());
    }

    private static char directionSymbol(Direction.AxisDirection dir){
        return dir == Direction.AxisDirection.POSITIVE ? '+' : '-';
    }

	/**
	 * @param logicalPos {@link Vec4d} in original 4D world
	 * @return {@link Vec4d} in 3D projected slice
	 */
	public @NotNull Vec4d project(Vec4d logicalPos) {
		return new Vec4d(
			renderX.getAxis4().choose(logicalPos) * (double) renderX.getDirection().offset(),
			renderY.getAxis4().choose(logicalPos) * (double) renderY.getDirection().offset(),
			renderZ.getAxis4().choose(logicalPos) * (double) renderZ.getDirection().offset(),
			renderW.getAxis4().choose(logicalPos) * (double) renderW.getDirection().offset()
		);
	}

	/**
	 * @param renderPos {@link Vec4d} in 3D projected slice
	 * @return {@link Vec4d} in original 4D world
	 */
	public @NotNull Vec4d projectInverse(Vec4d renderPos) {
		return   Vec4d.of(renderX.getVector4()).multiply(renderPos.x)
			.add(Vec4d.of(renderY.getVector4()).multiply(renderPos.y))
			.add(Vec4d.of(renderZ.getVector4()).multiply(renderPos.z))
			.add(Vec4d.of(renderW.getVector4()).multiply(renderPos.w));
	}

	/**
	 * @param logicalPos {@link BlockPos4} in original 4D world
	 * @return {@link BlockPos4} in 3D projected slice
	 */
	public @NotNull BlockPos4<?, ?> project(BlockPos4<?, ?> logicalPos) {
		Vec4d renderCenterPos = this.project(logicalPos.toCenterPos4());
		return BlockPos4.newBlockPos4(renderCenterPos.x, renderCenterPos.y, renderCenterPos.z, renderCenterPos.w);
	}

	/**
	 * @param renderPos {@link BlockPos4} in 3D projected slice
	 * @return {@link BlockPos4} in original 4D world
	 */
	public @NotNull BlockPos4<?, ?> projectInverse(BlockPos4<?, ?> renderPos) {
		Vec4d logicalCenterPos = this.projectInverse(renderPos.toCenterPos4());
		return BlockPos4.newBlockPos4(logicalCenterPos.x, logicalCenterPos.y, logicalCenterPos.z, logicalCenterPos.w);
	}

	/**
	 * @param logicalPos {@link BlockPos} in original 4D world
	 * @return {@link BlockPos} in 3D projected slice
	 */
	public @NotNull BlockPos project(BlockPos logicalPos) {
		return project(BlockPos4.of(logicalPos)).asBlockPos();
	}

	/**
	 * @param renderPos {@link BlockPos} in 3D projected slice
	 * @return {@link BlockPos} in original 4D world
	 */
	public @NotNull BlockPos projectInverse(BlockPos renderPos) {
		return projectInverse(BlockPos4.of(renderPos)).asBlockPos();
	}

	/**
	 * @param renderHitResult {@link BlockHitResult} in 3D projected slice
	 * @return {@link BlockHitResult} in original 4D world
	 */
	public @NotNull BlockHitResult projectInverse(BlockHitResult renderHitResult) {
		Vec3d logicalPos = projectInverse(new Vec4d(renderHitResult.getPos())).toPos3();
		BlockPos logicalBlockPos = projectInverse(renderHitResult.getBlockPos());
		Direction logicalSide = projectInverse(renderHitResult.getSide());
		return renderHitResult.getType() == HitResult.Type.MISS
			? BlockHitResult.createMissed(logicalPos, logicalSide, logicalBlockPos)
			: new BlockHitResult(logicalPos, logicalSide, logicalBlockPos, renderHitResult.isInsideBlock(), renderHitResult.isAgainstWorldBorder());
	}

	/**
	 * @param renderDirection {@link Direction} in 3D projected slice
	 * @return {@link Direction} in original 4D world
	 */
	public @NotNull Direction projectInverse(Direction renderDirection) {
		Direction4 logicalDirection = getDirectionByAxis(renderDirection.getAxis());
		if (renderDirection.getDirection().offset() < 0)
			logicalDirection = logicalDirection.getOpposite4();
		return logicalDirection.asDirection();
	}

	private @NotNull Direction4 getDirectionByAxis(Direction.Axis logicalAxis) {
		Optional<Direction4> direction4 = Stream
			.of(renderX, renderY, renderZ, renderW)
			.filter(renderDirection -> renderDirection.getAxis().equals(logicalAxis))
			.findFirst();
		assert direction4.isPresent();
		return direction4.get();
	}

	public @NotNull Perspective4 rotateAround(
		Direction4 fixedDirection0,
		Direction4 fixedDirection1
	) {
		Direction.Axis fixedAxis0 = fixedDirection0.getAxis();
		Direction.Axis fixedAxis1 = fixedDirection1.getAxis();
		assert !fixedAxis0.equals(fixedAxis1);

		List<Direction.Axis> nonFixedAxes = Arrays.stream(Direction4Constants.Axis4Constants.VALUES)
			.filter(anyAxis -> !(anyAxis.equals(fixedAxis0) || anyAxis.equals(fixedAxis1)))
			.limit(2)
			.toList();
		assert nonFixedAxes.size() == 2;
		Direction.Axis
			nonFixedAxis0 = nonFixedAxes.getFirst(),
			nonFixedAxis1 = nonFixedAxes.getLast();
		Direction4
			nonFixedDirection0 = Direction4.asDirection4(this.getDirectionByAxis(nonFixedAxis0).rotateClockwise(fixedAxis0, fixedAxis1)),
			nonFixedDirection1 = Direction4.asDirection4(this.getDirectionByAxis(nonFixedAxis1).rotateClockwise(fixedAxis0, fixedAxis1));

		Direction.Axis renderXAxis = renderX.getAxis();
		Direction.Axis renderYAxis = renderY.getAxis();
		Direction.Axis renderZAxis = renderZ.getAxis();
		Direction.Axis renderWAxis = renderW.getAxis();
		return new Perspective4(
			renderXAxis.equals(nonFixedAxis0) ? nonFixedDirection0 : renderXAxis.equals(nonFixedAxis1) ? nonFixedDirection1 : renderX,
			renderYAxis.equals(nonFixedAxis0) ? nonFixedDirection0 : renderYAxis.equals(nonFixedAxis1) ? nonFixedDirection1 : renderY,
			renderZAxis.equals(nonFixedAxis0) ? nonFixedDirection0 : renderZAxis.equals(nonFixedAxis1) ? nonFixedDirection1 : renderZ,
			renderWAxis.equals(nonFixedAxis0) ? nonFixedDirection0 : renderWAxis.equals(nonFixedAxis1) ? nonFixedDirection1 : renderW
		);
	}
}
