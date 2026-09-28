package com.bling.jewelry;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Lets the inventory screen's jewelry slots read and write a player's {@link Worn} jewelry. */
public class WornContainer implements Container {
	private final Player player;

	public WornContainer(Player player) {
		this.player = player;
	}

	private static BodySlot slot(int index) {
		return BodySlot.values()[index];
	}

	@Override
	public int getContainerSize() {
		return Worn.SIZE;
	}

	@Override
	public boolean isEmpty() {
		return Worn.of(player).isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return Worn.get(player, slot(index));
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack left = getItem(index).copy();
		if (left.isEmpty() || count <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack taken = left.split(count);
		Worn.set(player, slot(index), left);
		return taken;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = getItem(index);
		Worn.set(player, slot(index), ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		Worn.set(player, slot(index), stack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		Worn.clear(player);
	}
}
