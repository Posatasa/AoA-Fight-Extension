package com.afe.container;

import com.afe.ArmorTransformer;
import com.afe.ModBlocks;
import com.afe.ModContainers;
import com.afe.tile.ArmorTransTableTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ArmorTransTableContainer extends Container {
    // Cell centers (in the 215x252 GUI texture) for the input and the four output slots.
    private static final int INPUT_CX = 56;
    private static final int INPUT_CY = 69;
    private static final int INPUT_SIZE = 37;

    private static final int[] OUTPUT_CX = {138, 176, 138, 176}; // helmet, chestplate, legs, boots
    private static final int[] OUTPUT_CY = {50, 50, 87, 87};
    private static final int OUTPUT_SIZE = 24;

    private static final int[] PLAYER_X = {14, 35, 57, 78, 99, 121, 142, 163, 185};
    private static final int[] PLAYER_Y = {137, 159, 182, 215};

    private final ArmorTransTableTile tile;

    /** Client-side constructor: builds a dummy tile so slot wiring and previews still work. */
    public ArmorTransTableContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, new ArmorTransTableTile());
    }

    public ArmorTransTableContainer(int windowId, PlayerInventory playerInv, ArmorTransTableTile tile) {
        super(ModContainers.ARMOR_TRANS_TABLE.get(), windowId);
        this.tile = tile;

        // Input slot (accepts AoA3 armor only).
        this.addSlot(new ArmorTransSlot(tile.getInputHandler(), 0,
                INPUT_CX, INPUT_CY, INPUT_SIZE, 2.1F, true, null));

        // Four output slots, in fixed order: helmet, chestplate, legs, boots.
        for (int i = 0; i < 4; i++) {
            this.addSlot(new ArmorTransSlot(tile.getResultHandler(), i,
                    OUTPUT_CX[i], OUTPUT_CY[i], OUTPUT_SIZE, 1.4F, false, this::consumeInput));
        }

        // Player inventory: 3 rows + hotbar.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, PLAYER_X[col], PLAYER_Y[row]));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, PLAYER_X[col], PLAYER_Y[3]));
        }
    }

    /** Consumes one input and lets the tile recompute its previews (which also clears them). */
    private void consumeInput() {
        this.tile.getInputHandler().extractItem(0, 1, false);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (this.tile == null || this.tile.getLevel() == null) {
            return true;
        }
        return this.tile.getLevel().getBlockState(this.tile.getBlockPos()).getBlock() == ModBlocks.ARMOR_TRANS_TABLE.get();
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index == 0) {
                // Input -> player inventory.
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index <= 4) {
                // Output -> player inventory, then consume the input.
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                this.consumeInput();
                return result;
            } else {
                // Player inventory -> input slot (AoA3 armor only).
                if (ArmorTransformer.getArmorSet(stack) != null) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }
}
