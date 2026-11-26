package com.afe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Core logic for converting a piece of AoA3 armor into another piece of the same set.
 */
public final class ArmorTransformer {
    /** Armor pieces in the fixed slot order used by the GUI: helmet, chestplate, legs, boots. */
    public static final String[] PARTS = {"helmet", "chestplate", "legs", "boots"};

    /**
     * AoA3 standalone headgear that must never be treated as a convertible set piece, even though
     * a couple of them happen to end in "_helmet". The skill helmets (helm_of_the_*) and the night
     * vision goggles do not match the "_helmet" suffix pattern, so they are excluded automatically;
     * the two diving helmets below are listed explicitly because they do match the pattern.
     */
    private static final Set<String> EXCLUDED = new HashSet<>(Arrays.asList(
            "night_vision_goggles",
            "face_mask",
            "oceanus_helmet",
            "sealord_helmet"
    ));

    private ArmorTransformer() {
    }

    /**
     * Returns the armor set prefix (e.g. "alacrity") if the stack is a convertible AoA3 armor
     * piece, or {@code null} otherwise.
     */
    public static String getArmorSet(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl == null || !"aoa3".equals(rl.getNamespace())) {
            return null;
        }
        String name = rl.getPath();
        for (String part : PARTS) {
            String suffix = "_" + part;
            if (name.endsWith(suffix)) {
                String set = name.substring(0, name.length() - suffix.length());
                if (set.isEmpty() || EXCLUDED.contains(name)) {
                    return null;
                }
                return set;
            }
        }
        return null;
    }

    /**
     * Returns the part index of the given armor stack (0=helmet .. 3=boots), or -1 if it is not a
     * recognizable armor piece.
     */
    public static int getPartIndex(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl == null) {
            return -1;
        }
        String name = rl.getPath();
        for (int i = 0; i < PARTS.length; i++) {
            if (name.endsWith("_" + PARTS[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Builds the conversion result for the requested part index. Enchantments (and any other NBT,
     * such as a custom name) are copied over, and durability is scaled proportionally to the input's
     * remaining durability.
     */
    public static ItemStack getResult(ItemStack input, int partIndex) {
        String set = getArmorSet(input);
        if (set == null) {
            return ItemStack.EMPTY;
        }
        // Do not offer the input's own part: converting helmet -> helmet is pointless.
        if (partIndex == getPartIndex(input)) {
            return ItemStack.EMPTY;
        }
        Item target = ForgeRegistries.ITEMS.getValue(new ResourceLocation("aoa3", set + "_" + PARTS[partIndex]));
        if (target == null || target == Items.AIR) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(target);
        if (input.hasTag()) {
            result.setTag(input.getTag().copy());
        }
        if (input.isDamageableItem() && result.isDamageableItem()) {
            int inputMax = input.getMaxDamage();
            int resultMax = result.getMaxDamage();
            if (inputMax > 0 && resultMax > 0) {
                float ratio = (float) input.getDamageValue() / (float) inputMax;
                result.setDamageValue(Math.round(ratio * resultMax));
            }
        }
        return result;
    }

    /** True when every piece of the set exists in the registry (used to keep the previews sane). */
    public static boolean isFullSet(String set) {
        if (set == null) {
            return false;
        }
        for (String part : PARTS) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("aoa3", set + "_" + part));
            if (item == null || item == Items.AIR) {
                return false;
            }
        }
        return true;
    }
}
