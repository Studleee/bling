package com.bling.client.render;

import org.jspecify.annotations.Nullable;

import com.bling.Bling;
import com.bling.item.JewelryItem;
import com.bling.jewelry.Piece;
import com.bling.jewelry.Worn;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;

/** Draws the jewelry a player is wearing onto their model. */
public class JewelryLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	/** Filled in for each player as they're drawn (see AvatarRendererMixin). */
	public static final RenderStateDataKey<Worn> WORN = RenderStateDataKey.create(() -> "bling:worn");

	private final JewelryModels wide = JewelryModels.create(false);
	private final JewelryModels slim = JewelryModels.create(true);

	public JewelryLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
		super(renderer);
	}

	private static Identifier texture(String name) {
		return Bling.id("textures/entity/jewelry/" + name + ".png");
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
		Worn worn = state.getData(WORN);
		if (worn == null || state.isInvisible) {
			return;
		}
		JewelryModels models = state.skin.model() == PlayerModelType.SLIM ? slim : wide;
		for (ItemStack stack : worn.stacks()) {
			if (!(stack.getItem() instanceof JewelryItem jewelry)) {
				continue;
			}
			JewelryModels.Parts parts = models.get(jewelry.piece());
			submit(collector, parts.metal(), jewelry.metal().id(), poseStack, lightCoords, state);
			String accent = accent(jewelry);
			if (accent != null) {
				submit(collector, parts.accent(), accent, poseStack, lightCoords, state);
			}
			if (parts.diamonds() != null) {
				submit(collector, parts.diamonds(), "diamond", poseStack, lightCoords, state);
			}
		}
	}

	private static void submit(SubmitNodeCollector collector, PlayerModel model, String texture, PoseStack poseStack, int lightCoords,
		AvatarRenderState state) {
		collector.submitModel(model, state, poseStack, RenderTypes.entityCutout(texture(texture)), lightCoords, OverlayTexture.NO_OVERLAY,
			state.outlineColor);
	}

	/** The texture of a piece's accent: the watch face, an iced-out chain's diamonds, or its gem (none if plain). */
	private static @Nullable String accent(JewelryItem jewelry) {
		if (jewelry.piece().isWatch()) {
			return "watch_face";
		}
		if (jewelry.piece() == Piece.ICED_CHAIN) {
			return "diamond";
		}
		return jewelry.gem() == null ? null : jewelry.gem().id();
	}
}
