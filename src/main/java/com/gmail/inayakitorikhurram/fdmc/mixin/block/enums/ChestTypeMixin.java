package com.gmail.inayakitorikhurram.fdmc.mixin.block.enums;

import com.gmail.inayakitorikhurram.fdmc.block.enums.ChestType4;
import com.gmail.inayakitorikhurram.fdmc.block.enums.ChestType4Enum;
import com.gmail.inayakitorikhurram.fdmc.block.enums.ChestTypeConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.enums.ChestType.*;

@Mixin(ChestType.class)
public abstract class ChestTypeMixin implements StringIdentifiable, ChestType4 {
    @Shadow @Final
    private String name;

    @Mutable @Final
    private ChestType4Enum enumEquivalent;

    //VALUES
    @Shadow @Final @Mutable
    private static ChestType[] field_12573;

    private static final ChestType KATA = fdmc$addChestType("kata", "kata");
    private static final ChestType ANA = fdmc$addChestType("ana", "ana");

    private static ChestType fdmc$addChestType(String internalName, String name) {
        assert field_12573 != null; //hopefully
        ChestType dir = fdmc$invokeInit(internalName, field_12573.length, name);
        field_12573 = ArrayUtils.add(field_12573, dir);
        return dir;
    }

    @Invoker("<init>")
    private static ChestType fdmc$invokeInit(String internalName, int internalId, String name) {
        throw new AssertionError();
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    private void initEnumEquivalent(String string, int i, String name, CallbackInfo ci) {
        enumEquivalent = ChestType4Enum.byId(((ChestType)(Object)this).ordinal());
    }

    @Override
    public ChestType4Enum asEnum() {
        return enumEquivalent;
    }

    //@Override
    public ChestType getOpposite() {

        return switch (this.asEnum()) {
            case SINGLE -> SINGLE;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case KATA -> ANA;
            case ANA -> KATA;
            default -> throw new IncompatibleClassChangeError();
        };
    }
}
