package com.bling.jewelry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bling.Bling;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The jewelry a player is wearing, one stack per {@link BodySlot}. Saved with the player and sent to every
 * client that can see them, so everyone sees each other's jewelry. Never changed in place: every change
 * stores a new copy, which is what triggers the sync.
 */
public record Worn(List<ItemStack> stacks) {
	public static final int SIZE = BodySlot.values().length;
	public static final Worn EMPTY = new Worn(Collections.nCopies(SIZE, ItemStack.EMPTY));

	private static final Codec<Worn> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(Worn::of, Worn::stacks);
	private static final StreamCodec<RegistryFriendlyByteBuf, Worn> STREAM_CODEC =
		ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(Worn::of, Worn::stacks);

	public static final AttachmentType<Worn> ATTACHMENT = AttachmentRegistry.create(
		Bling.id("worn"),
		builder -> builder
			.initializer(() -> EMPTY)
			.persistent(CODEC)
			.copyOnDeath()
			.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all())
	);

	private static Worn of(List<ItemStack> stacks) {
		List<ItemStack> sized = new ArrayList<>(SIZE);
		for (int i = 0; i < SIZE; i++) {
			sized.add(i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
		}
		return new Worn(List.copyOf(sized));
	}

	public ItemStack get(BodySlot slot) {
		return stacks.get(slot.ordinal());
	}

	public boolean isEmpty() {
		return stacks.stream().allMatch(ItemStack::isEmpty);
	}

	public static Worn of(Player player) {
		return player.getAttachedOrElse(ATTACHMENT, EMPTY);
	}

	public static ItemStack get(Player player, BodySlot slot) {
		return of(player).get(slot);
	}

	public static void set(Player player, BodySlot slot, ItemStack stack) {
		List<ItemStack> stacks = new ArrayList<>(of(player).stacks);
		stacks.set(slot.ordinal(), stack);
		player.setAttached(ATTACHMENT, new Worn(List.copyOf(stacks)));
	}

	public static void clear(Player player) {
		player.setAttached(ATTACHMENT, EMPTY);
	}

	public static void initialize() {
	}
}
