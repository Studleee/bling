package com.bling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.bling.jewelry.JewelrySlot;
import com.bling.jewelry.Worn;

import net.minecraft.server.network.ServerGamePacketListenerImpl;

/** Creative mode sends inventory changes by slot number, and only accepts up to the offhand slot. */
@Mixin(ServerGamePacketListenerImpl.class)
abstract class ServerGamePacketListenerImplMixin {
	@ModifyConstant(method = "handleSetCreativeModeSlot", constant = @Constant(intValue = 45))
	private int bling$acceptJewelrySlots(int lastSlot) {
		return JewelrySlot.FIRST_INDEX + Worn.SIZE - 1;
	}
}
