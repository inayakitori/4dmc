package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockCollisionSpliterator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(method = "wouldCollideAt", at = @At("RETURN"), cancellable = true)
    public void wouldCollideAt(BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValueZ() || !((CanStep)this).isStepping()){
            return;
        }
        BlockPos prevPosOffset = FDMCMath.getOffset(((CanStep)this).getStepDirection() * -1);
        pos = pos.add(prevPosOffset);

        this.world.getChunk(pos);
        BlockState definitelyStone = this.world.getBlockState(pos);

        Box box = this.getBoundingBox().offset(prevPosOffset);
        Box box2 = new Box(pos.getX(), box.minY, pos.getZ(), (double)pos.getX() + 1.0, box.maxY, (double)pos.getZ() + 1.0).contract(1.0E-7);
        boolean wouldCollide = this.world.canCollide(null, box2);
        cir.setReturnValue(wouldCollide);
    }


}
