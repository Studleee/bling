package com.bling.registry;

import com.bling.block.JewelersBenchBlock;
import com.bling.block.JewelryDisplayCaseBlock;
import com.bling.block.JewelryStandBlock;
import com.bling.jewelry.BodySlot;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * All of your blocks. Strength is how long it takes to mine (dirt is 0.5, stone is 1.5, obsidian is 50).
 */
public final class ModBlocks {
	public static final Block JEWELERS_BENCH = Register.block(
		"jewelers_bench",
		JewelersBenchBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD)
	);
	public static final Block JEWELRY_DISPLAY_CASE = Register.block(
		"jewelry_display_case",
		JewelryDisplayCaseBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).noOcclusion()
	);
	public static final Block WATCH_STAND = stand("watch_stand", BodySlot.WRIST);
	public static final Block NECKLACE_BUST = stand("necklace_bust", BodySlot.NECK);
	public static final Block EARRING_STAND = stand("earring_stand", BodySlot.EARS);

	private static Block stand(String name, BodySlot slot) {
		return Register.block(
			name,
			properties -> new JewelryStandBlock(slot, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD).noOcclusion()
		);
	}

	private ModBlocks() {
	}

	public static void initialize() {
	}
}
