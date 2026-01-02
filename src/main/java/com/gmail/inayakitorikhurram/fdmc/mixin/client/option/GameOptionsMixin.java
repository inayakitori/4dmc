package com.gmail.inayakitorikhurram.fdmc.mixin.client.option;

import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements GameOptions4 {
    @Unique
    Perspective4 perspective4 = new Perspective4(
        Direction4Constants.EAST4,
        Direction4Constants.UP4,
        Direction4Constants.SOUTH4,
        Direction4Constants.ANA4
    );

    @Override
    public Perspective4 getPerspective4() {
        return this.perspective4;
    }

    @Override
    public void setPerspective4(Perspective4 perspective4) {
        this.perspective4 = perspective4;
    }
}
