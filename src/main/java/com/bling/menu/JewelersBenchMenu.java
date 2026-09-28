package com.bling.menu;

import org.jspecify.annotations.Nullable;

import com.bling.item.JewelryItem;
import com.bling.jewelry.Gem;
import com.bling.jewelry.Metal;
import com.bling.jewelry.Piece;
import com.bling.registry.ModBlocks;
import com.bling.registry.ModItems;
import com.bling.registry.ModMenus;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The jeweler's bench: a metal slot, a gem slot, a button per {@link Piece}, and a result slot.
 * Pick a piece, put in enough ingots (and a gem if you want one), and take the jewelry out.
 */
public class JewelersBenchMenu extends AbstractContainerMenu {
	public static final int METAL_SLOT = 0;
	public static final int GEM_SLOT = 1;
	public static final int RESULT_SLOT = 2;
	private static final int INV_START = 3;
	private static final int HOTBAR_START = 30;
	private static final int INV_END = 39;

	private static final Identifier EMPTY_METAL = Identifier.withDefaultNamespace("container/slot/ingot");
	private static final Identifier EMPTY_GEM = Identifier.withDefaultNamespace("container/slot/diamond");

	private final ContainerLevelAccess access;
	private final DataSlot selected = DataSlot.standalone();
	private final Container inputs = new SimpleContainer(2) {
		@Override
		public void setChanged() {
			super.setChanged();
			JewelersBenchMenu.this.slotsChanged(this);
		}
	};
	private final ResultContainer result = new ResultContainer();
	private long lastSoundTime;

	public JewelersBenchMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, ContainerLevelAccess.NULL);
	}

	public JewelersBenchMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
		super(ModMenus.JEWELERS_BENCH, containerId);
		this.access = access;
		selected.set(-1);

		addSlot(new Slot(inputs, METAL_SLOT, 20, 24) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Metal.fromIngot(stack) != null;
			}

			@Override
			public Identifier getNoItemIcon() {
				return EMPTY_METAL;
			}
		});
		addSlot(new Slot(inputs, GEM_SLOT, 20, 46) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Gem.fromItem(stack) != null;
			}

			@Override
			public Identifier getNoItemIcon() {
				return EMPTY_GEM;
			}
		});
		addSlot(new Slot(result, RESULT_SLOT, 143, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}

			@Override
			public void onTake(Player player, ItemStack taken) {
				Piece piece = selectedPiece();
				if (piece != null) {
					inputs.removeItem(METAL_SLOT, piece.ingots());
					if (piece.gemUse() != Piece.GemUse.NONE && !inputs.getItem(GEM_SLOT).isEmpty()) {
						inputs.removeItem(GEM_SLOT, piece.gems());
					}
				}
				access.execute((level, pos) -> {
					long time = level.getGameTime();
					if (lastSoundTime != time) {
						level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.3F);
						lastSoundTime = time;
					}
				});
				super.onTake(player, taken);
			}
		});

		addStandardInventorySlots(inventory, 8, 84);
		addDataSlot(selected);
	}

	public @Nullable Piece selectedPiece() {
		int index = selected.get();
		return index >= 0 && index < Piece.values().length ? Piece.values()[index] : null;
	}

	public @Nullable Metal metal() {
		return Metal.fromIngot(inputs.getItem(METAL_SLOT));
	}

	public @Nullable Gem gem() {
		return Gem.fromItem(inputs.getItem(GEM_SLOT));
	}

	/** The gem this piece would get from the gem slot (null for plain, watches, and iced-out pieces). */
	private @Nullable Gem gemFor(Piece piece) {
		return piece.hasGemVariants() ? gem() : null;
	}

	/**
	 * The jewelry this piece would come out as with what's in the slots now. With empty slots it shows iron,
	 * and pieces that need a gem show a diamond.
	 */
	public ItemStack preview(Piece piece) {
		Metal metal = metal();
		Gem gem = gemFor(piece);
		if (gem == null && piece.gemUse() == Piece.GemUse.REQUIRED) {
			gem = Gem.DIAMOND;
		}
		JewelryItem item = ModItems.get(piece, metal == null ? Metal.IRON : metal, gem);
		return item == null ? ItemStack.EMPTY : new ItemStack(item);
	}

	/** Whether the slots hold enough to make this piece right now. */
	public boolean canMake(Piece piece) {
		ItemStack ingots = inputs.getItem(METAL_SLOT);
		if (metal() == null || ingots.getCount() < piece.ingots()) {
			return false;
		}
		ItemStack gems = inputs.getItem(GEM_SLOT);
		return switch (piece.gemUse()) {
			case NONE -> gems.isEmpty();
			case OPTIONAL -> true;
			case REQUIRED -> gem() != null;
			case ICED -> gem() == Gem.DIAMOND && gems.getCount() >= piece.gems();
		};
	}

	@Override
	public boolean clickMenuButton(Player player, int buttonId) {
		if (buttonId < 0 || buttonId >= Piece.values().length) {
			return false;
		}
		selected.set(buttonId);
		updateResult();
		return true;
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		if (container == inputs) {
			updateResult();
		}
	}

	private void updateResult() {
		Piece piece = selectedPiece();
		if (piece != null && canMake(piece)) {
			JewelryItem item = ModItems.get(piece, metal(), gemFor(piece));
			result.setItem(0, item == null ? ItemStack.EMPTY : new ItemStack(item));
		} else {
			result.setItem(0, ItemStack.EMPTY);
		}
		broadcastChanges();
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(access, player, ModBlocks.JEWELERS_BENCH);
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
		return target.container != result && super.canTakeItemForPickAll(carried, target);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		Slot slot = slots.get(slotIndex);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack clicked = stack.copy();
		if (slotIndex == RESULT_SLOT) {
			if (!moveItemStackTo(stack, INV_START, INV_END, true)) {
				return ItemStack.EMPTY;
			}
			slot.onQuickCraft(stack, clicked);
		} else if (slotIndex < INV_START) {
			if (!moveItemStackTo(stack, INV_START, INV_END, false)) {
				return ItemStack.EMPTY;
			}
		} else if (Metal.fromIngot(stack) != null) {
			if (!moveItemStackTo(stack, METAL_SLOT, METAL_SLOT + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (Gem.fromItem(stack) != null) {
			if (!moveItemStackTo(stack, GEM_SLOT, GEM_SLOT + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (slotIndex < HOTBAR_START) {
			if (!moveItemStackTo(stack, HOTBAR_START, INV_END, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveItemStackTo(stack, INV_START, HOTBAR_START, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		}
		slot.setChanged();
		if (stack.getCount() == clicked.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(player, stack);
		broadcastChanges();
		return clicked;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		result.removeItemNoUpdate(0);
		access.execute((level, pos) -> clearContainer(player, inputs));
	}
}
