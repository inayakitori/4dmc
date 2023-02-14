package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.SuffocationSupport;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.UnderSupport;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CanStep{

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public boolean step(int moveDirection) {
        if(canStep(moveDirection)) {

            SupportHandler supportHandler = getSupportHandler();

            ServerPlayerEntity actualThis = (ServerPlayerEntity) (Object) this;

            Vec3d vel = getVelocity();
            setSteppingGlobally(actualThis, moveDirection, vel);
            //write to client-side buffer
            Vec3d oldPos = getPos();
            Vec3d newPos = oldPos.add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
            //place a block underneath player and clear stone
            supportHandler.queueSupport(UnderSupport.class, actualThis, new BlockPos(newPos), new BlockPos(oldPos));
            supportHandler.queueSupport(SuffocationSupport.class, actualThis, new BlockPos(newPos), new BlockPos(oldPos));

            //actually tp player
            teleport(newPos.x, newPos.y, newPos.z);
            return true;
        } else{
            return false;
        }
    }



}
