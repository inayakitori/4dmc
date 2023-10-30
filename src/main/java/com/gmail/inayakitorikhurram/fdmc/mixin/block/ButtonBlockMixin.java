package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.util.VoxelShapeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.util.VoxelShapeProvider.*;
import static com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants.*;
import static net.minecraft.block.enums.BlockFace.*;
import static net.minecraft.block.HorizontalFacingBlock.FACING;
import static net.minecraft.block.WallMountedBlock.FACE;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin {

    @Shadow @Final public static BooleanProperty POWERED;

    private static final float BUTTON_MARGIN = 0.0f;
    private static final VoxelShape CEILING_W_SHAPE = Block.createCuboidShape(
            5.0 - BUTTON_MARGIN,  14.0 - BUTTON_MARGIN,  5.0 - BUTTON_MARGIN,
            11.0 + BUTTON_MARGIN,  16.0 + BUTTON_MARGIN, 11.0 + BUTTON_MARGIN);
    private static final VoxelShape CEILING_W_PRESSED_SHAPE = Block.createCuboidShape(
            5.0- BUTTON_MARGIN, 15.0- BUTTON_MARGIN,   5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 16.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape KATA_SHAPE = Block.createCuboidShape(
            5.0- BUTTON_MARGIN,  6.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 10.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape KATA_PRESSED_SHAPE =  Block.createCuboidShape(
            5.0- BUTTON_MARGIN, 7.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 9.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape ANA_SHAPE = Block.createCuboidShape(
            5.0- BUTTON_MARGIN,  6.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 10.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape ANA_PRESSED_SHAPE = Block.createCuboidShape(
            5.0- BUTTON_MARGIN, 7.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 9.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape FLOOR_W_SHAPE = Block.createCuboidShape(
            5.0- BUTTON_MARGIN, 0.0- BUTTON_MARGIN,  5.0- BUTTON_MARGIN,
            11.0+ BUTTON_MARGIN, 2.0+ BUTTON_MARGIN, 11.0+ BUTTON_MARGIN);
    private static final VoxelShape FLOOR_W_PRESSED_SHAPE = Block.createCuboidShape(
            5.0, 0.0,  5.0,
            11.0, 1.0, 11.0);

    private final VoxelShapeProvider shapeProvider = VoxelShapeProvider.builder((Block)(Object) this)
            .set(newSwitch(FACING).addCase(KATA, ANA,
                    newSwitch(FACE)
                            .addCase(FLOOR, boolSwitch(POWERED, supply(FLOOR_W_PRESSED_SHAPE), supply(FLOOR_W_SHAPE)))
                            .addCase(CEILING, boolSwitch(POWERED, supply(CEILING_W_PRESSED_SHAPE), supply(CEILING_W_SHAPE)))
                            .addCase(WALL,
                                    newSwitch(FACING)
                                            .addCase(KATA, boolSwitch(POWERED, supply(KATA_PRESSED_SHAPE), supply(KATA_SHAPE)))
                                            .addCase(ANA, boolSwitch(POWERED, supply(ANA_PRESSED_SHAPE), supply(ANA_SHAPE)))
                            )
            )).build();

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void fdmc$getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        shapeProvider.apply(state).ifPresent(cir::setReturnValue);
    }
}
