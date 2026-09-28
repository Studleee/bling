package com.bling.jewelry;

import com.bling.item.JewelryItem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** One of the jewelry slots in the inventory screen. Only takes jewelry meant for that part of the body. */
public class JewelrySlot extends Slot {
	/** The inventory screen's slot index of the first jewelry slot; they come right after the offhand slot. */
	public static final int FIRST_INDEX = 46;
	/** Where the slots sit in the survival inventory: a column just above the offhand slot. */
	public static final int X = 77;
	public static final int Y = 8;

	private final BodySlot bodySlot;

	public JewelrySlot(WornContainer container, BodySlot bodySlot) {
		super(container, bodySlot.ordinal(), X, Y + bodySlot.ordinal() * 18);
		this.bodySlot = bodySlot;
	}

	public static boolean fits(ItemStack stack, BodySlot bodySlot) {
		return stack.getItem() instanceof JewelryItem jewelry && jewelry.piece().slot() == bodySlot;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return fits(stack, bodySlot);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public Identifier getNoItemIcon() {
		return bodySlot.emptyIcon();
	}
}
