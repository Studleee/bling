package com.bling.jewelry;

import com.bling.item.JewelryItem;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Unlisted perk: a gold chain is heavy. Wearing one gives Slowness I and Resistance I for as long as it's on. */
public final class GoldChainWeight {
	private static final int CHECK_INTERVAL = 10;

	private GoldChainWeight() {
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % CHECK_INTERVAL != 0) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				boolean wearing = Worn.get(player, BodySlot.NECK).getItem() instanceof JewelryItem jewelry && jewelry.metal() == Metal.GOLD;
				apply(player, MobEffects.SLOWNESS, wearing);
				apply(player, MobEffects.RESISTANCE, wearing);
			}
		});
	}

	private static void apply(ServerPlayer player, Holder<MobEffect> effect, boolean wearing) {
		MobEffectInstance current = player.getEffect(effect);
		if (wearing && current == null) {
			player.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, 0, true, false, true));
		} else if (!wearing && current != null && isOurs(current)) {
			player.removeEffect(effect);
		}
	}

	// Only strip the chain's own effect, never one from a potion or beacon.
	private static boolean isOurs(MobEffectInstance effect) {
		return effect.isInfiniteDuration() && effect.isAmbient() && !effect.isVisible() && effect.getAmplifier() == 0;
	}
}
