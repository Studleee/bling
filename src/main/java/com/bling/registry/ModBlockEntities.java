package com.bling.registry;

import java.util.Set;

import com.bling.Bling;
import com.bling.block.JewelryDisplayCaseBlockEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<JewelryDisplayCaseBlockEntity> JEWELRY_DISPLAY_CASE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Bling.id("jewelry_display_case"),
		new BlockEntityType<>(JewelryDisplayCaseBlockEntity::new, Set.of(ModBlocks.JEWELRY_DISPLAY_CASE))
	);

	private ModBlockEntities() {
	}

	public static void initialize() {
	}
}
