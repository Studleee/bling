package com.bling.jewelry;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The metals jewelry is made from. Each piece costs a number of ingots of its metal. */
public enum Metal {
	IRON("iron", "Iron", Items.IRON_INGOT),
	GOLD("gold", "Gold", Items.GOLD_INGOT),
	COPPER("copper", "Copper", Items.COPPER_INGOT),
	NETHERITE("netherite", "Netherite", Items.NETHERITE_INGOT);

	private final String id;
	private final String displayName;
	private final Item ingot;

	Metal(String id, String displayName, Item ingot) {
		this.id = id;
		this.displayName = displayName;
		this.ingot = ingot;
	}

	public String id() {
		return id;
	}

	public String displayName() {
		return displayName;
	}

	public Item ingot() {
		return ingot;
	}

	public static @Nullable Metal fromIngot(ItemStack stack) {
		for (Metal metal : values()) {
			if (stack.is(metal.ingot)) {
				return metal;
			}
		}
		return null;
	}
}
