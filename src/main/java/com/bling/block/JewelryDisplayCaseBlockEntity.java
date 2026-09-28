package com.bling.block;

import com.bling.item.JewelryItem;
import com.bling.menu.JewelryDisplayCaseMenu;
import com.bling.registry.ModBlockEntities;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ListBackedContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.slf4j.Logger;

/** Holds the display case's jewelry and sends it to nearby players so they can see it on the velvet. */
public class JewelryDisplayCaseBlockEntity extends BlockEntity implements ListBackedContainer, MenuProvider {
	public static final int SIZE = 4;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Component TITLE = Component.translatable("container.bling.jewelry_display_case");

	private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

	public JewelryDisplayCaseBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.JEWELRY_DISPLAY_CASE, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		ContainerHelper.loadAllItems(input, items);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items, true);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(problemPath(), LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
			ContainerHelper.saveAllItems(output, items, true);
			return output.buildResult();
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean acceptsItemType(ItemStack stack) {
		return stack.getItem() instanceof JewelryItem;
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public Component getDisplayName() {
		return TITLE;
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new JewelryDisplayCaseMenu(containerId, inventory, this);
	}
}
