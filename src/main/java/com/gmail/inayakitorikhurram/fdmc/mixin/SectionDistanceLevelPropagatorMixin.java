package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.SectionDistanceLevelPropagator;
import net.minecraft.world.chunk.light.LevelPropagator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//Again untested code, it's possible none of this works :c
@Mixin(SectionDistanceLevelPropagator.class)
abstract class SectionDistanceLevelPropagatorMixin extends LevelPropagator {
    protected SectionDistanceLevelPropagatorMixin(int levelCount, int expectedLevelSize, int expectedTotalSize) {
        super(levelCount, expectedLevelSize, expectedTotalSize);
    }

    //probably don't need this rn

    @Inject(method = "propagateLevel(JIZ)V", at = @At("TAIL"))
    protected void propagateLevel(long id, int level, boolean decrease, CallbackInfo ci) {
        for(int dw = -1; dw <= 1; dw= dw+2) {
            long l = ChunkSectionPos.offset(id, dw * FDMCConstants.STEP_DISTANCE, 0, 0);
            if (l != id) {
                this.propagateLevel(id, l, level, decrease);
            }
        }

    }

    @Inject(method = "recalculateLevel(JJI)I", at = @At("RETURN"), cancellable = true)
    protected void recalculateLevel(long id, long excludedId, int maxLevel, CallbackInfoReturnable<Integer> cir) {
        int currentLevel = Math.min(maxLevel, cir.getReturnValue());

        for(int dw = -1; dw <= 1; dw = dw+2) {
            long adjacentSection = ChunkSectionPos.offset(id, dw * FDMCConstants.STEP_DISTANCE, 0, 0);
            if (adjacentSection == id) {
                adjacentSection = Long.MAX_VALUE;
            }
            if (adjacentSection != excludedId){
                int levelFromAdjChunk = this.getPropagatedLevel(adjacentSection, id, this.getLevel(adjacentSection));
                if (currentLevel > levelFromAdjChunk) {
                    currentLevel = levelFromAdjChunk;
                }
            }
            if (currentLevel == 0){
                cir.setReturnValue(currentLevel);
            }
        }
        cir.setReturnValue(currentLevel);
    }
}
