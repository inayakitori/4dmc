package com.gmail.inayakitorikhurram.fdmc.mixin.item;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ItemSettings4;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Mixin(Items.class)
public class ItemsMixin {

    // Slice before the field so we can use ordinal = 0 & don't have to worry about checking the ordinal every MC update
//    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/item/Items;REDSTONE_TORCH:Lnet/minecraft/item/Item;", shift = At.Shift.BEFORE)),
//            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/item/Items;register(Lnet/minecraft/block/Block;Ljava/util/function/BiFunction;)Lnet/minecraft/item/Item;"),
//    index = 1)
//    private static BiFunction<Block, Item.Settings, Item> modifySettingsRedstoneTorch(BiFunction<Block, Item.Settings, Item> factory) {
//        return ((Block block, Item.Settings settings) -> factory.apply(block, MixinUtil.use4DProperties(settings)));
//    }

    // turns every BlockItem into one that uses the 4D properties
    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Lnet/minecraft/block/Block;Ljava/util/function/BiFunction;)Lnet/minecraft/item/Item;"),
            index = 1)
    private static BiFunction<Block, Item.Settings, Item> modifyBlockItems(BiFunction<Block, Item.Settings, Item> factory) {
        return (Block block, Item.Settings settings) -> {
            ItemSettings4 itemSettings4 = ((ItemSettings4) settings).apply((BlockSettings4Access) block);
            //FDMCConstants.LOGGER.info("modifying item settings for block {} settings {}", block, settings);
            return factory.apply(block, (Item.Settings) itemSettings4);
        };
    }

    @ModifyArg(method = "<clinit>",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;REDSTONE_TORCH:Lnet/minecraft/block/Block;", opcode = Opcodes.GETSTATIC)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Lnet/minecraft/block/Block;Ljava/util/function/BiFunction;)Lnet/minecraft/item/Item;", ordinal = 0),
            index = 1)
    private static BiFunction<Block, Item.Settings, Item> modifyTorchBlockItems(BiFunction<Block, Item.Settings, Item> factory) {
        return (Block block, Item.Settings settings) -> {
            //FDMCConstants.LOGGER.info("modifying item settings for block {} (torch)", block.getLootTableKey());
            return factory.apply(block, MixinUtil.use4DProperties(settings));
        };
    }

    @Redirect(method = "<clinit>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Lnet/minecraft/block/Block;)Lnet/minecraft/item/Item;")
    )
    private static Item modifyRegisterArgsBlock(Block block){
        return Items.register(block, BlockItem::new, (Item.Settings) ((ItemSettings4) new Item.Settings()).apply((BlockSettings4Access) block));
    }


    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Lnet/minecraft/block/Block;Ljava/util/function/UnaryOperator;)Lnet/minecraft/item/Item;"))
    private static Item modifyRegisterArgsBlockUnary(Block block, UnaryOperator<Item.Settings> settingsOperator){
        //FDMCConstants.LOGGER.info("modifying item settings for block {} (unary)", block.getTranslationKey());
        return Items.register(block, (settings) -> (Item.Settings) ((ItemSettings4) settings).apply((BlockSettings4Access) block));
    }

// jank af
    @Redirect(method = "<clinit>",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;REDSTONE_WIRE:Lnet/minecraft/block/Block;", opcode = Opcodes.GETSTATIC)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Ljava/util/function/Function;)Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item modifyRegisterArgsIdFunction(String id, Function<Item.Settings, Item> factory){
        //FDMCConstants.LOGGER.info("modifying item settings for block {} (redstone wire)", id);
        return Items.register(id, factory, (Item.Settings) ((ItemSettings4) new Item.Settings()).apply((BlockSettings4Access) Blocks.REDSTONE_WIRE));
    }


    //TODO
//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ScaffoldingItem;<init>(Lnet/minecraft/block/Block;Lnet/minecraft/item/Item$Settings;)V"))
//    private static Item.Settings modifySettingsScaffolding(Item.Settings settings) {
//        return MixinUtil.use4DProperties(settings);
//    }
}
