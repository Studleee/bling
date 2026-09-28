package com.bling.client.render;

import org.jspecify.annotations.Nullable;

import com.bling.block.JewelryStandBlock;
import com.bling.block.JewelryStandBlockEntity;
import com.bling.jewelry.BodySlot;
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

/** Shows the piece on a stand upright against its front: on the watch cushion, the bust's chest, or under the earring bar. */
public class JewelryStandRenderer implements BlockEntityRenderer<JewelryStandBlockEntity, JewelryStandRenderer.State> {
	private final ItemModelResolver itemModelResolver;

	public JewelryStandRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(
		JewelryStandBlockEntity blockEntity,
		State state,
		float partialTicks,
		Vec3 cameraPosition,
		ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.facing = blockEntity.getBlockState().getValue(JewelryStandBlock.FACING);
		state.slot = blockEntity.getBlockState().getBlock() instanceof JewelryStandBlock stand ? stand.slot() : BodySlot.WRIST;
		ItemStack stack = blockEntity.getItem(0);
		if (stack.isEmpty()) {
			state.item = null;
		} else {
			state.item = new ItemStackRenderState();
			int seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());
			itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
		}
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.item == null) {
			return;
		}
		// Center height, distance in front of the block's middle, and size, all in pixels (16 to a block).
		float[] place = switch (state.slot) {
			case WRIST -> new float[] { 3.8F, 1.6F, 5.3F };
			case NECK -> new float[] { 8.0F, 1.6F, 7.2F };
			case EARS -> new float[] { 5.5F, 0.6F, 8.0F };
		};
		poseStack.pushPose();
		poseStack.translate(0.5F, place[0] / 16.0F, 0.5F);
		poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
		poseStack.translate(0.0F, 0.0F, place[1] / 16.0F);
		// The fixed item transform faces the item backwards (item frames turn it around the same way).
		poseStack.rotateDegrees(Axis.YP, 180.0F);
		float size = place[2] / 16.0F;
		poseStack.scale(size, size, size);
		state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	public static class State extends BlockEntityRenderState {
		public @Nullable ItemStackRenderState item;
		public Direction facing = Direction.NORTH;
		public BodySlot slot = BodySlot.WRIST;
	}
}
