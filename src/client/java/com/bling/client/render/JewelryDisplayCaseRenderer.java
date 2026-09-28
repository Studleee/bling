package com.bling.client.render;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.bling.block.JewelryDisplayCaseBlock;
import com.bling.block.JewelryDisplayCaseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/** Lays the display case's jewelry flat on the velvet, spread out to fit however many pieces there are. */
public class JewelryDisplayCaseRenderer implements BlockEntityRenderer<JewelryDisplayCaseBlockEntity, JewelryDisplayCaseRenderer.State> {
	/** Height of the velvet in the block model (11 pixels up), plus a hair so the items don't sink into it. */
	private static final float VELVET_Y = 11.0F / 16.0F + 0.01F;
	private static final float ITEM_SIZE = 0.3F;
	/** Where each piece sits for 1, 2, 3, and 4 pieces: x across the case, z toward the back (negative) or front. */
	private static final float[][][] LAYOUTS = {
		{ { 0.0F, 0.0F } },
		{ { -0.19F, 0.0F }, { 0.19F, 0.0F } },
		{ { -0.3F, 0.0F }, { 0.0F, 0.0F }, { 0.3F, 0.0F } },
		{ { -0.18F, -0.17F }, { 0.18F, -0.17F }, { -0.18F, 0.17F }, { 0.18F, 0.17F } }
	};

	private final ItemModelResolver itemModelResolver;

	public JewelryDisplayCaseRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(
		JewelryDisplayCaseBlockEntity blockEntity,
		State state,
		float partialTicks,
		Vec3 cameraPosition,
		ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.facing = blockEntity.getBlockState().getValue(JewelryDisplayCaseBlock.FACING);
		state.items.clear();
		int seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());
		for (int slot = 0; slot < blockEntity.getContainerSize(); slot++) {
			ItemStack stack = blockEntity.getItem(slot);
			if (!stack.isEmpty()) {
				ItemStackRenderState item = new ItemStackRenderState();
				itemModelResolver.updateForTopItem(item, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed + slot);
				state.items.add(item);
			}
		}
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.items.isEmpty()) {
			return;
		}
		float[][] layout = LAYOUTS[state.items.size() - 1];
		for (int i = 0; i < state.items.size(); i++) {
			poseStack.pushPose();
			poseStack.translate(0.5F, VELVET_Y, 0.5F);
			poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
			poseStack.translate(layout[i][0], 0.0F, layout[i][1]);
			// Lie the item face-up with its top pointing to the back of the case, so it reads right from the front.
			// The extra half turn is because the fixed item transform faces the item backwards (item frames do the same).
			poseStack.rotateDegrees(Axis.XP, -90.0F);
			poseStack.rotateDegrees(Axis.YP, 180.0F);
			poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
			state.items.get(i).submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
	}

	public static class State extends BlockEntityRenderState {
		public final List<ItemStackRenderState> items = new ArrayList<>(JewelryDisplayCaseBlockEntity.SIZE);
		public Direction facing = Direction.NORTH;
	}
}
