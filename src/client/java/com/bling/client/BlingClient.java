package com.bling.client;

import com.bling.Bling;
import com.bling.client.render.JewelryDisplayCaseRenderer;
import com.bling.client.render.JewelryLayer;
import com.bling.client.screen.JewelersBenchScreen;
import com.bling.client.screen.JewelryDisplayCaseScreen;
import com.bling.registry.ModBlockEntities;
import com.bling.registry.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

public class BlingClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.JEWELERS_BENCH, JewelersBenchScreen::new);
		MenuScreens.register(ModMenus.JEWELRY_DISPLAY_CASE, JewelryDisplayCaseScreen::new);
		BlockEntityRenderers.register(ModBlockEntities.JEWELRY_DISPLAY_CASE, JewelryDisplayCaseRenderer::new);

		LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
			if (renderer instanceof AvatarRenderer<?> avatarRenderer) {
				helper.register(new JewelryLayer(avatarRenderer));
			}
		});

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Bling.id("watch"), WatchHud::extract);
	}
}
