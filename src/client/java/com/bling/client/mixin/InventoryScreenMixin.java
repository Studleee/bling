package com.bling.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bling.jewelry.JewelrySlot;
import com.bling.jewelry.Worn;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

/** The survival inventory's background has no slots above the offhand slot, so draw the jewelry slots there. */
@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
	private static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");

	private InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
		super(menu, recipeBook, inventory, title);
	}

	@Inject(method = "extractBackground", at = @At("TAIL"))
	private void bling$drawJewelrySlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		for (int i = 0; i < Worn.SIZE; i++) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, leftPos + JewelrySlot.X - 1, topPos + JewelrySlot.Y - 1 + i * 18, 18, 18);
		}
	}
}
