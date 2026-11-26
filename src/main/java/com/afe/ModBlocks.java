package com.afe;

import com.afe.block.ArmorTransTableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Afe.MOD_ID);

    public static final RegistryObject<Block> ARMOR_TRANS_TABLE = BLOCKS.register("armor_trans_table",
            () -> new ArmorTransTableBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)));

    private ModBlocks() {
    }
}
