package com.bling.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bling.client.render.JewelryLayer;
import com.bling.jewelry.Worn;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;

/** Copies each player's worn jewelry into their render state so {@link JewelryLayer} can draw it. */
@Mixin(AvatarRenderer.class)
abstract class AvatarRendererMixin {
	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
		at = @At("TAIL")
	)
	private void bling$extractJewelry(Avatar avatar, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		state.setData(JewelryLayer.WORN, avatar instanceof Player player ? Worn.of(player) : null);
	}
}
