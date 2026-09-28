package com.bling.jewelry;

import com.bling.Bling;

import net.minecraft.resources.Identifier;

/** The three jewelry slots next to the player in the inventory, top to bottom. */
public enum BodySlot {
	EARS("ears"),
	NECK("neck"),
	WRIST("wrist");

	private final Identifier emptyIcon;

	BodySlot(String name) {
		this.emptyIcon = Bling.id("container/slot/" + name);
	}

	/** The faded outline shown in the slot while it's empty. */
	public Identifier emptyIcon() {
		return emptyIcon;
	}
}
