package com.gmail.inayakitorikhurram.fdmc.client.option;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Specifies how logical axes are projected onto render axes.
 */
@Environment(value = EnvType.CLIENT)
public record Perspective4 (
	Direction4 renderX,
	Direction4 renderY,
	Direction4 renderZ,
	Direction4 renderW
) {
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
	 * @param renderPos {@link BlockPos4} in 3D projected slice
	 * @return {@link BlockPos4} in original 4D world
	 */
	public @NotNull BlockPos4<?, ?> projectInverse(BlockPos4<?, ?> renderPos) {
		Vec4d logicalCenterPos = this.projectInverse(renderPos.toCenterPos4());
		return BlockPos4.newBlockPos4(logicalCenterPos.x, logicalCenterPos.y, logicalCenterPos.z, logicalCenterPos.w);
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
