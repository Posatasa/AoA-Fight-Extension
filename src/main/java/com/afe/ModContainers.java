package com.afe;

import com.afe.container.ArmorTransTableContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Afe.MOD_ID);

    public static final RegistryObject<ContainerType<ArmorTransTableContainer>> ARMOR_TRANS_TABLE =
            CONTAINERS.register("armor_trans_table",
                    () -> new ContainerType<>(ArmorTransTableContainer::new));

    private ModContainers() {
    }
}
