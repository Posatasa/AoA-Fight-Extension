package com.convtable;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ConvTable.MOD_ID);

    /** Dedicated creative tab for this mod, with the table itself as the icon. */
    public static final ItemGroup TAB = new ItemGroup("convtable") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ARMOR_TRANS_TABLE.get());
        }
    };

    public static final RegistryObject<Item> ARMOR_TRANS_TABLE = ITEMS.register("armor_trans_table",
            () -> new BlockItem(ModBlocks.ARMOR_TRANS_TABLE.get(), new Item.Properties().tab(TAB)));

    private ModItems() {
    }
}
