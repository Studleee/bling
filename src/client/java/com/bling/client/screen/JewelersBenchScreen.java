package com.bling.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.bling.Bling;
import com.bling.jewelry.Metal;
import com.bling.jewelry.Piece;
import com.bling.menu.JewelersBenchMenu;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** The jeweler's bench: metal and gem slots on the left, a button per piece, and the result on the right. */
public class JewelersBenchScreen extends AbstractContainerScreen<JewelersBenchMenu> {
	private static final Identifier BACKGROUND = Bling.id("textures/gui/container/jewelers_bench.png");
	private static final Identifier BUTTON = Bling.id("container/jewelers_bench/button");
	private static final Identifier BUTTON_HIGHLIGHTED = Bling.id("container/jewelers_bench/button_highlighted");
	private static final Identifier BUTTON_SELECTED = Bling.id("container/jewelers_bench/button_selected");
	private static final int BUTTONS_X = 46;
	private static final int BUTTONS_Y = 24;
	private static final int BUTTON_SIZE = 20;
	private static final int BUTTON_COLUMNS = 4;

	public JewelersBenchScreen(JewelersBenchMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	private int buttonAt(double mouseX, double mouseY) {
		double x = mouseX - (leftPos + BUTTONS_X);
		double y = mouseY - (topPos + BUTTONS_Y);
		if (x < 0 || y < 0 || x >= BUTTON_COLUMNS * BUTTON_SIZE) {
			return -1;
		}
		int index = (int) (y / BUTTON_SIZE) * BUTTON_COLUMNS + (int) (x / BUTTON_SIZE);
		return index < Piece.values().length ? index : -1;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

		int hovered = buttonAt(mouseX, mouseY);
		Piece selected = menu.selectedPiece();
		for (Piece piece : Piece.values()) {
			int x = leftPos + BUTTONS_X + piece.ordinal() % BUTTON_COLUMNS * BUTTON_SIZE;
			int y = topPos + BUTTONS_Y + piece.ordinal() / BUTTON_COLUMNS * BUTTON_SIZE;
			Identifier sprite = piece == selected ? BUTTON_SELECTED : piece.ordinal() == hovered ? BUTTON_HIGHLIGHTED : BUTTON;
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, BUTTON_SIZE, BUTTON_SIZE);
			graphics.fakeItem(menu.preview(piece), x + 2, y + 2);
			if (!menu.canMake(piece)) {
				graphics.fill(x + 2, y + 2, x + 18, y + 18, 0x908b8b8b);
			}
		}
		if (hovered >= 0) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractTooltip(graphics, mouseX, mouseY);
		int hovered = buttonAt(mouseX, mouseY);
		if (hovered < 0) {
			return;
		}
		Piece piece = Piece.values()[hovered];
		Metal metal = menu.metal();
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable("piece.bling." + piece.id()));
		Component ingots = metal == null
			? Component.translatable("gui.bling.any_ingots", piece.ingots())
			: Component.translatable("gui.bling.ingots", piece.ingots(), metal.ingot().getDefaultInstance().getHoverName());
		lines.add(ingots.copy().withStyle(ChatFormatting.GRAY));
		Component gems = switch (piece.gemUse()) {
			case NONE -> Component.translatable("gui.bling.no_gem");
			case OPTIONAL -> Component.translatable("gui.bling.gem_optional");
			case REQUIRED -> Component.translatable("gui.bling.gem_required");
			case ICED -> Component.translatable("gui.bling.diamonds", piece.gems());
		};
		lines.add(gems.copy().withStyle(ChatFormatting.GRAY));
		graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		int index = buttonAt(event.x(), event.y());
		if (index >= 0 && menu.clickMenuButton(minecraft.player, index)) {
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}
}
