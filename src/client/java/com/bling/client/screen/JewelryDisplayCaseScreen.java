package com.bling.client.screen;

import com.bling.Bling;
import com.bling.menu.JewelryDisplayCaseMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** The display case: 4 jewelry slots on a strip of velvet, above the player's inventory. */
public class JewelryDisplayCaseScreen extends AbstractContainerScreen<JewelryDisplayCaseMenu> {
	private static final Identifier BACKGROUND = Bling.id("textures/gui/container/jewelry_display_case.png");

	public JewelryDisplayCaseScreen(JewelryDisplayCaseMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 133);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}
}
