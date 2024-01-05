/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.gmail.inayakitorikhurram.fdmc.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FDMCContainerScreen
extends HandledScreen<FDMCScreenHandler>
implements ScreenHandlerProvider<FDMCScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int rows;
    private final int columns;

    public FDMCContainerScreen(FDMCScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.rows = handler.getRows();
        this.columns = handler.getColumns();
        this.backgroundWidth = this.columns * 18 + 14;
        this.backgroundHeight = 114 + this.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 83;
        this.playerInventoryTitleX = 8 + (this.backgroundWidth - 175) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        @SuppressWarnings("unused")
		MatrixStack matrices = context.getMatrices();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int chestOffsetX = (this.width - this.backgroundWidth) / 2;
        int chestOffsetY = (this.height - this.backgroundHeight) / 2;
        //chest gui
        int offsetPerChest = 162;
        int chestRows = Math.floorDiv(this.columns, 9);
        //left
        context.drawTexture(TEXTURE, chestOffsetX, chestOffsetY, 0, 0, 7, this.rows * 18 + 17);
        context.drawTexture(TEXTURE, chestOffsetX, chestOffsetY + 125 , 0, 215, 7, 20);
        //chests
        for(int chestRow = chestRows - 1; chestRow >= 0; chestRow --){
            context.drawTexture(TEXTURE, 7 + chestOffsetX + offsetPerChest * chestRow, chestOffsetY, 7, 0, offsetPerChest, this.rows * 18 + 17);
            context.drawTexture(TEXTURE, 7 + chestOffsetX + offsetPerChest * chestRow, chestOffsetY + 125, 7, 215, offsetPerChest, 20);
        }
        //right
        context.drawTexture(TEXTURE, 7 + chestOffsetX + offsetPerChest * chestRows, chestOffsetY, 169, 0, 10, this.rows * 18 + 17);
        context.drawTexture(TEXTURE, 7 + chestOffsetX + offsetPerChest * chestRows, chestOffsetY + 125 , 169, 215, 10, 20);

        int inventoryWidth = 175;
        int inventoryOffsetX = (this.width - inventoryWidth) / 2;
        //inventory
        context.drawTexture(TEXTURE, inventoryOffsetX, chestOffsetY + this.rows * 18 + 25, 0, 0, inventoryWidth, 16);
        context.drawTexture(TEXTURE, inventoryOffsetX, chestOffsetY + this.rows * 18 + 41, 0, 139, inventoryWidth, 96);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

}

