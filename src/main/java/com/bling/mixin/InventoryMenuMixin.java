package com.bling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bling.item.JewelryItem;
import com.bling.jewelry.BodySlot;
import com.bling.jewelry.JewelrySlot;
import com.bling.jewelry.WornContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Adds the three jewelry slots to the player's inventory, and lets shift-click put jewelry on. */
@Mixin(InventoryMenu.class)
abstract class InventoryMenuMixin extends AbstractCraftingMenu {
	private InventoryMenuMixin(MenuType<?> menuType, int containerId, int width, int height) {
		super(menuType, containerId, width, height);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void bling$addJewelrySlots(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
		WornContainer worn = new WornContainer(owner);
		for (BodySlot bodySlot : BodySlot.values()) {
			addSlot(new JewelrySlot(worn, bodySlot));
		}
	}

	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void bling$shiftClickOn(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
		if (slotIndex < InventoryMenu.INV_SLOT_START || slotIndex >= InventoryMenu.USE_ROW_SLOT_END) {
			return;
		}
		Slot from = slots.get(slotIndex);
		if (from.getItem().getItem() instanceof JewelryItem jewelry) {
			Slot to = slots.get(JewelrySlot.FIRST_INDEX + jewelry.piece().slot().ordinal());
			if (!to.hasItem()) {
				to.setByPlayer(from.getItem().copyWithCount(1));
				from.remove(1);
				from.setChanged();
				cir.setReturnValue(ItemStack.EMPTY);
			}
		}
	}
}
