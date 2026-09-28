package com.bling.menu;

import com.bling.block.JewelryDisplayCaseBlockEntity;
import com.bling.item.JewelryItem;
import com.bling.registry.ModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The display case's screen: a row of 4 jewelry-only slots above the player's inventory, laid out like a hopper. */
public class JewelryDisplayCaseMenu extends AbstractContainerMenu {
	private static final int SIZE = JewelryDisplayCaseBlockEntity.SIZE;
	private static final int INV_END = SIZE + 36;

	private final Container container;

	public JewelryDisplayCaseMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(SIZE));
	}

	public JewelryDisplayCaseMenu(int containerId, Inventory inventory, Container container) {
		super(ModMenus.JEWELRY_DISPLAY_CASE, containerId);
		this.container = container;
		checkContainerSize(container, SIZE);
		container.startOpen(inventory.player);

		for (int i = 0; i < SIZE; i++) {
			addSlot(new Slot(container, i, 53 + i * 18, 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.getItem() instanceof JewelryItem;
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}
			});
		}
		addStandardInventorySlots(inventory, 8, 51);
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		Slot slot = slots.get(slotIndex);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack clicked = stack.copy();
		if (slotIndex < SIZE) {
			if (!moveItemStackTo(stack, SIZE, INV_END, true)) {
				return ItemStack.EMPTY;
			}
		} else if (!(stack.getItem() instanceof JewelryItem) || !moveItemStackTo(stack, 0, SIZE, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return clicked;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		container.stopOpen(player);
	}
}
