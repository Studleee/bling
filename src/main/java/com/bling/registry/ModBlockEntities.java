package com.bling.registry;

import java.util.Set;

import com.bling.Bling;
import com.bling.block.JewelryDisplayCaseBlockEntity;
import com.bling.block.JewelryStandBlockEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<JewelryDisplayCaseBlockEntity> JEWELRY_DISPLAY_CASE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Bling.id("jewelry_display_case"),
		new BlockEntityType<>(JewelryDisplayCaseBlockEntity::new, Set.of(ModBlocks.JEWELRY_DISPLAY_CASE))
	);
	public static final BlockEntityType<JewelryStandBlockEntity> JEWELRY_STAND = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Bling.id("jewelry_stand"),
		new BlockEntityType<>(JewelryStandBlockEntity::new, Set.of(ModBlocks.WATCH_STAND, ModBlocks.NECKLACE_BUST, ModBlocks.EARRING_STAND))
	);

	private ModBlockEntities() {
	}

	public static void initialize() {
	}
}
