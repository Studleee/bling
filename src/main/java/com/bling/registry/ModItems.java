package com.bling.registry;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.bling.item.JewelryItem;
import com.bling.jewelry.Gem;
import com.bling.jewelry.Metal;
import com.bling.jewelry.Piece;

import net.minecraft.world.item.Item;

/**
 * Every piece of jewelry: each piece in each metal, plain and/or with each gem, depending on the piece.
 * Ids look like {@code gold_earrings}, {@code gold_diamond_studs}, or {@code gold_iced_chain}.
 */
public final class ModItems {
	private static final Map<String, JewelryItem> JEWELRY = new HashMap<>();

	static {
		for (Piece piece : Piece.values()) {
			for (Metal metal : Metal.values()) {
				if (piece.gemUse() != Piece.GemUse.REQUIRED) {
					register(piece, metal, null);
				}
				if (piece.hasGemVariants()) {
					for (Gem gem : Gem.values()) {
						register(piece, metal, gem);
					}
				}
			}
		}
	}

	private ModItems() {
	}

	public static String id(Piece piece, Metal metal, @Nullable Gem gem) {
		return metal.id() + "_" + (gem == null ? "" : gem.id() + "_") + piece.id();
	}

	private static void register(Piece piece, Metal metal, @Nullable Gem gem) {
		String id = id(piece, metal, gem);
		Item.Properties properties = new Item.Properties().stacksTo(1);
		if (metal == Metal.NETHERITE) {
			properties.fireResistant();
		}
		JEWELRY.put(id, (JewelryItem) Register.item(id, p -> new JewelryItem(piece, metal, gem, p), properties));
	}

	/** The jewelry item for this combination, or null if it doesn't exist (like a watch with a gem). */
	public static @Nullable JewelryItem get(Piece piece, Metal metal, @Nullable Gem gem) {
		return JEWELRY.get(id(piece, metal, gem));
	}

	public static void initialize() {
	}
}
