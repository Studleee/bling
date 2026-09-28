package com.bling.client;

import com.bling.item.JewelryItem;
import com.bling.jewelry.BodySlot;
import com.bling.jewelry.Piece;
import com.bling.jewelry.Worn;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/** While you wear a watch, the time of day shows in the top-left corner of the screen. */
public final class WatchHud {
	private WatchHud() {
	}

	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.level == null || minecraft.getDebugOverlay().showDebugScreen()) {
			return;
		}
		ItemStack wrist = Worn.get(minecraft.player, BodySlot.WRIST);
		if (!(wrist.getItem() instanceof JewelryItem jewelry) || !jewelry.piece().isWatch()) {
			return;
		}
		graphics.item(wrist, 4, 4);
		graphics.text(minecraft.font, time(minecraft.level.getOverworldClockTime()), 23, 8, 0xFFFFFFFF, true);
	}

	/** Day time 0 is 6:00 AM; a day is 24000 ticks. */
	static String time(long dayTicks) {
		long ticks = Math.floorMod(dayTicks, 24000L);
		int hours = (int) ((ticks / 1000 + 6) % 24);
		int minutes = (int) (ticks % 1000 * 60 / 1000);
		String suffix = hours < 12 ? "AM" : "PM";
		int shown = hours % 12 == 0 ? 12 : hours % 12;
		return String.format("%d:%02d %s", shown, minutes, suffix);
	}
}
