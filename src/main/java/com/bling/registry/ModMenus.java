package com.bling.registry;

import com.bling.Bling;
import com.bling.menu.JewelersBenchMenu;
import com.bling.menu.JewelryDisplayCaseMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
	public static final MenuType<JewelersBenchMenu> JEWELERS_BENCH = Registry.register(
		BuiltInRegistries.MENU,
		Bling.id("jewelers_bench"),
		new MenuType<>(JewelersBenchMenu::new, FeatureFlags.VANILLA_SET)
	);
	public static final MenuType<JewelryDisplayCaseMenu> JEWELRY_DISPLAY_CASE = Registry.register(
		BuiltInRegistries.MENU,
		Bling.id("jewelry_display_case"),
		new MenuType<>(JewelryDisplayCaseMenu::new, FeatureFlags.VANILLA_SET)
	);

	private ModMenus() {
	}

	public static void initialize() {
	}
}
