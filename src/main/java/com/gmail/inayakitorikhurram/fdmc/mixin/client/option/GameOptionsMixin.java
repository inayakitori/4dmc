package com.gmail.inayakitorikhurram.fdmc.mixin.client.option;

import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4Enum;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements GameOptions4 {
    @Unique
    Perspective4Enum perspective4 = Perspective4Enum.XYZ;

    @Override
    public Perspective4Enum getPerspective4() {
        return this.perspective4;
    }

    @Override
    public void setPerspective4(Perspective4Enum perspective4) {
        this.perspective4 = perspective4;
    }
}
