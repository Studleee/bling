package com.bling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bling.jewelry.Worn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

/** Jewelry drops on death like the rest of the inventory, unless keepInventory is on. */
@Mixin(Player.class)
abstract class PlayerMixin {
	@Inject(method = "dropEquipment", at = @At("TAIL"))
	private void bling$dropJewelry(ServerLevel level, CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
			return;
		}
		for (ItemStack stack : Worn.of(player).stacks()) {
			if (!stack.isEmpty()) {
				ItemEntity drop = player.createItemStackToDrop(stack, true, false);
				if (drop != null) {
					level.addFreshEntity(drop);
				}
			}
		}
		Worn.clear(player);
	}
}
