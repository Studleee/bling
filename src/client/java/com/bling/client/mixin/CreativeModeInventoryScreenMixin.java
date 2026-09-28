package com.bling.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.bling.jewelry.JewelrySlot;
import com.bling.jewelry.Worn;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;

/**
 * The creative inventory tab lays out the player's inventory slots itself, by slot number. Put the jewelry
 * slots in a row to the right of the armor slots and draw their backgrounds.
 */
@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
	private static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");
	private static final int FIRST_X = 127;
	private static final int Y = 20;

	@Shadow
	private static CreativeModeTab selectedTab;

	private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@ModifyArgs(
		method = "selectTab",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;<init>(Lnet/minecraft/world/inventory/Slot;III)V"
		)
	)
	private void bling$placeJewelrySlots(Args args) {
		int index = args.get(1);
		int jewelry = index - JewelrySlot.FIRST_INDEX;
		if (jewelry >= 0 && jewelry < Worn.SIZE) {
			args.set(2, FIRST_X + jewelry * 18);
			args.set(3, Y);
		}
	}

	@Inject(method = "extractBackground", at = @At("TAIL"))
	private void bling$drawJewelrySlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
			for (int i = 0; i < Worn.SIZE; i++) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, leftPos + FIRST_X - 1 + i * 18, topPos + Y - 1, 18, 18);
			}
		}
	}
}
