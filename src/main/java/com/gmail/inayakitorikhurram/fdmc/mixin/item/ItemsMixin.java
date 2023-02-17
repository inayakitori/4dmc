package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Items.class)
public class ItemsMixin {
    @ModifyArgs(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;<init>(Lnet/minecraft/block/Block;Lnet/minecraft/item/Item$Settings;)V"))
    private static void copyOverBlockSettings(Args args) {
        args.<ItemSettings4>get(1).apply(args.get(0)); // very readable, much wow
    }

    // Slice before the field so we can use ordinal = 0 & don't have to worry about checking the ordinal every MC update
    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/item/Items;REDSTONE_TORCH:Lnet/minecraft/item/Item;", shift = At.Shift.BY, by = -3)),
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/item/VerticallyAttachableBlockItem;<init>(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/item/Item$Settings;Lnet/minecraft/util/math/Direction;)V"))
    private static Item.Settings modifySettingsRedstoneTorch(Item.Settings settings) {
        return MixinUtil.use4DProperties(settings);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ScaffoldingItem;<init>(Lnet/minecraft/block/Block;Lnet/minecraft/item/Item$Settings;)V"))
    private static Item.Settings modifySettingsScaffolding(Item.Settings settings) {
        return MixinUtil.use4DProperties(settings);
    }
}
