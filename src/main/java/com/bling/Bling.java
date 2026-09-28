package com.bling;

import com.bling.jewelry.GoldChainWeight;
import com.bling.jewelry.Worn;
import com.bling.registry.ModBlockEntities;
import com.bling.registry.ModBlocks;
import com.bling.registry.ModCreativeTab;
import com.bling.registry.ModItems;
import com.bling.registry.ModMenus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bling implements ModInitializer {
	public static final String MOD_ID = "bling";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Worn.initialize();
		GoldChainWeight.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModItems.initialize();
		ModMenus.initialize();
		ModCreativeTab.initialize();
		LOGGER.info("{} loaded", MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
