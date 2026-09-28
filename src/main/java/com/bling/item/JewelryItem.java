package com.bling.item;

import org.jspecify.annotations.Nullable;

import com.bling.jewelry.Gem;
import com.bling.jewelry.Metal;
import com.bling.jewelry.Piece;
import com.bling.jewelry.Worn;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A piece of jewelry. Right-click to put it on (swapping out whatever was in that slot). */
public class JewelryItem extends Item {
	private final Piece piece;
	private final Metal metal;
	private final @Nullable Gem gem;

	public JewelryItem(Piece piece, Metal metal, @Nullable Gem gem, Properties properties) {
		super(properties);
		this.piece = piece;
		this.metal = metal;
		this.gem = gem;
	}

	public Piece piece() {
		return piece;
	}

	public Metal metal() {
		return metal;
	}

	public @Nullable Gem gem() {
		return gem;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide()) {
			ItemStack held = player.getItemInHand(hand);
			ItemStack previous = Worn.get(player, piece.slot());
			Worn.set(player, piece.slot(), held.copyWithCount(1));
			held.shrink(1);
			if (!previous.isEmpty()) {
				if (held.isEmpty()) {
					player.setItemInHand(hand, previous);
				} else if (!player.getInventory().add(previous)) {
					player.drop(previous, false, Prediction.SERVER_ONLY);
				}
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
				metal == Metal.GOLD ? SoundEvents.ARMOR_EQUIP_GOLD : SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.PLAYERS, 1.0F, 1.2F);
		}
		return InteractionResult.SUCCESS;
	}
}
