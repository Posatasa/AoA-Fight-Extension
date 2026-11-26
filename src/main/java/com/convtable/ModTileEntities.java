package com.convtable;

import com.convtable.tile.ArmorTransTableTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ConvTable.MOD_ID);

    public static final RegistryObject<TileEntityType<ArmorTransTableTile>> ARMOR_TRANS_TABLE =
            TILES.register("armor_trans_table",
                    () -> TileEntityType.Builder.of(ArmorTransTableTile::new, ModBlocks.ARMOR_TRANS_TABLE.get())
                            .build(null));

    private ModTileEntities() {
    }
}
