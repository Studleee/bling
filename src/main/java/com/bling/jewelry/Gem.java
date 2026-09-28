package com.bling.jewelry;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Gems that can be set into earrings, chains, and bracelets. One gem per piece. */
public enum Gem {
	DIAMOND("diamond", "Diamond", Items.DIAMOND),
	EMERALD("emerald", "Emerald", Items.EMERALD),
	AMETHYST("amethyst", "Amethyst", Items.AMETHYST_SHARD);

	private final String id;
	private final String displayName;
	private final Item item;

	Gem(String id, String displayName, Item item) {
		this.id = id;
		this.displayName = displayName;
		this.item = item;
	}

	public String id() {
		return id;
	}

	public String displayName() {
		return displayName;
	}

	public Item item() {
		return item;
	}

	public static @Nullable Gem fromItem(ItemStack stack) {
		for (Gem gem : values()) {
			if (stack.is(gem.item)) {
				return gem;
			}
		}
		return null;
	}
}
