package com.bling.registry;

import com.bling.Bling;
import com.bling.jewelry.Gem;
import com.bling.jewelry.Metal;
import com.bling.jewelry.Piece;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(
		BuiltInRegistries.CREATIVE_MODE_TAB.key(),
		Bling.id(Bling.MOD_ID)
	);

	private ModCreativeTab() {
	}

	public static void initialize() {
		CreativeModeTab tab = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(ModItems.get(Piece.CHAIN, Metal.GOLD, Gem.DIAMOND)))
			.title(Component.translatable("creativeTab." + Bling.MOD_ID))
			.displayItems((params, output) -> Register.creativeTabItems().forEach(output::accept))
			.build();

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, tab);
	}
}
