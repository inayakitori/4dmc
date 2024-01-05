package com.gmail.inayakitorikhurram.fdmc.mixin.world;


import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;register(Ljava/lang/String;Lnet/minecraft/world/GameRules$Category;Lnet/minecraft/world/GameRules$Type;)Lnet/minecraft/world/GameRules$Key;"))
    private static void modifySpawnRadius(Args args){
        if(Objects.equals(args.get(0).toString(), "spawnRadius")){
            args.set(2, GameRules.IntRule.create(5));
            //System.out.println("changed spawn radius to 5");
        }
    }

}
