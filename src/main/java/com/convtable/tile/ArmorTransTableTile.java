package com.convtable.tile;

import com.convtable.ArmorTransformer;
import com.convtable.ModTileEntities;
import com.convtable.container.ArmorTransTableContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ArmorTransTableTile extends TileEntity implements INamedContainerProvider {
    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            ArmorTransTableTile.this.setChanged();
            ArmorTransTableTile.this.recomputeOutputs();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return ArmorTransformer.getArmorSet(stack) != null;
        }
    };

    /**
     * Derived previews for helmet/chestplate/legs/boots. Recomputed whenever the input changes,
     * so taking the input back out always clears the previews (no stale, take-able results).
     */
    private final ItemStackHandler resultHandler = new ItemStackHandler(4);

    private final LazyOptional<IItemHandler> inputCap = LazyOptional.of(() -> inputHandler);

    public ArmorTransTableTile() {
        super(ModTileEntities.ARMOR_TRANS_TABLE.get());
    }

    public IItemHandler getInputHandler() {
        return inputHandler;
    }

    public IItemHandler getResultHandler() {
        return resultHandler;
    }

    private void recomputeOutputs() {
        ItemStack input = inputHandler.getStackInSlot(0);
        for (int i = 0; i < 4; i++) {
            resultHandler.setStackInSlot(i, ArmorTransformer.getResult(input, i));
        }
    }

    public void dropContents() {
        if (this.level != null && !this.level.isClientSide) {
            ItemStack stack = inputHandler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                double x = this.worldPosition.getX() + 0.5D;
                double y = this.worldPosition.getY() + 0.5D;
                double z = this.worldPosition.getZ() + 0.5D;
                this.level.addFreshEntity(new ItemEntity(this.level, x, y, z, stack));
            }
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.convtable.armor_trans_table");
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
        return new ArmorTransTableContainer(windowId, playerInv, this);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put("input", inputHandler.serializeNBT());
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("input")) {
            inputHandler.deserializeNBT(tag.getCompound("input"));
        }
        recomputeOutputs();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inputCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        inputCap.invalidate();
    }
}
