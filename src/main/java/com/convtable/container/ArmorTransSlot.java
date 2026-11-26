package com.convtable.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * A slot in the Armor Trans Table GUI. It carries the geometry of the (large/medium) cell it sits
 * in so the screen can render the item scaled and centered, instead of as a standard 16px icon.
 *
 * <p>The screen suppresses the standard 16px draw for these slots via {@link #suppressRender}, so an
 * item is never rendered twice (once small, once scaled).</p>
 */
public class ArmorTransSlot extends SlotItemHandler {
    private final int cellCenterX;
    private final int cellCenterY;
    private final int cellSize;
    private final float itemScale;
    private final boolean input;
    private final Runnable onTake;

    /** Set only for the duration of the standard render pass, so it draws nothing for this slot. */
    public boolean suppressRender = false;

    public ArmorTransSlot(IItemHandler handler, int index, int cellCenterX, int cellCenterY,
                          int cellSize, float itemScale, boolean input, Runnable onTake) {
        super(handler, index, cellCenterX - 8, cellCenterY - 8);
        this.cellCenterX = cellCenterX;
        this.cellCenterY = cellCenterY;
        this.cellSize = cellSize;
        this.itemScale = itemScale;
        this.input = input;
        this.onTake = onTake;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return input && com.convtable.ArmorTransformer.getArmorSet(stack) != null;
    }

    @Override
    public ItemStack getItem() {
        return suppressRender ? ItemStack.EMPTY : super.getItem();
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack) {
        if (onTake != null) {
            onTake.run();
        }
        return stack;
    }

    public int getCellCenterX() {
        return cellCenterX;
    }

    public int getCellCenterY() {
        return cellCenterY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public float getItemScale() {
        return itemScale;
    }
}
