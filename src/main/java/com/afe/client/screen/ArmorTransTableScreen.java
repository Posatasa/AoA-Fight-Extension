package com.afe.client.screen;

import com.afe.Afe;
import com.afe.container.ArmorTransSlot;
import com.afe.container.ArmorTransTableContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ArmorTransTableScreen extends ContainerScreen<ArmorTransTableContainer> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Afe.MOD_ID, "textures/gui/armor_trans_table.png");

    private static final int TEXTURE_W = 215;
    private static final int TEXTURE_H = 252;

    public ArmorTransTableScreen(ArmorTransTableContainer menu, PlayerInventory inv, ITextComponent title) {
        super(menu, inv, title);
        this.imageWidth = TEXTURE_W;
        this.imageHeight = TEXTURE_H;
    }

    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEXTURE_W, TEXTURE_H);
    }

    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY) {
        String title = this.title.getString();
        int width = this.font.width(title);
        this.font.drawShadow(ms, title, (this.imageWidth - width) / 2.0F, 10, 0xFFFFFF);
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        // Suppress the normal 16px draw for the input/output slots so the item is not drawn twice.
        for (int i = 0; i < 5; i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot instanceof ArmorTransSlot) {
                ((ArmorTransSlot) slot).suppressRender = true;
            }
        }
        super.render(ms, mouseX, mouseY, partialTicks);
        for (int i = 0; i < 5; i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot instanceof ArmorTransSlot) {
                ((ArmorTransSlot) slot).suppressRender = false;
            }
        }

        // Draw the enlarged, centered icons for the input and the four outputs.
        for (int i = 0; i < 5; i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot instanceof ArmorTransSlot && !slot.getItem().isEmpty()) {
                ArmorTransSlot s = (ArmorTransSlot) slot;
                renderScaledItem(ms, slot.getItem(), s.getCellCenterX(), s.getCellCenterY(), s.getItemScale());
            }
        }

        // The standard tooltip pass saw an empty stack for these slots, so draw their tooltip here.
        if (this.hoveredSlot instanceof ArmorTransSlot && this.hoveredSlot.hasItem()) {
            this.renderTooltip(ms, this.hoveredSlot.getItem(), mouseX, mouseY);
        }
    }

    private void renderScaledItem(MatrixStack ms, ItemStack stack, int centerX, int centerY, float scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.leftPos + centerX, this.topPos + centerY, 100.0F);
        RenderSystem.scalef(scale, scale, 1.0F);
        this.itemRenderer.renderGuiItem(stack, -8, -8);
        // Also draw the decorations (durability bar / stack count) so damaged armor shows its bar.
        this.itemRenderer.renderGuiItemDecorations(this.font, stack, -8, -8);
        RenderSystem.popMatrix();
    }
}
