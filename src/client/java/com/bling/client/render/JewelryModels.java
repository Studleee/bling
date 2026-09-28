package com.bling.client.render;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import com.bling.jewelry.Piece;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.player.PlayerModel;

/**
 * The 3D jewelry worn on the player, built from small boxes attached to the head, body, and left arm so it
 * moves with them. Units are skin pixels: the head spans -4..4 across, the body's front face is at z = -2,
 * and the left arm hangs from its shoulder with the wrist around y = 8.
 * <p>
 * Each piece has a metal model (textured with its metal), an accent model (its gem, the watch face, or an
 * iced-out chain's diamonds), and sometimes extra diamonds (the iced-out watch's bezel and band).
 */
public final class JewelryModels {
	public record Parts(PlayerModel metal, PlayerModel accent, @Nullable PlayerModel diamonds) {
	}

	private final Map<Piece, Parts> models = new EnumMap<>(Piece.class);

	private JewelryModels(boolean slim) {
		float arm = slim ? 3.0F : 4.0F;
		float outside = arm - 1.0F;

		models.put(Piece.EARRINGS, parts(slim, "head",
			root -> {
				for (float side : new float[] {4.5F, -5.0F}) {
					root.addBox(side, -3.5F, -1.0F, 0.5F, 0.5F, 2.0F)
						.addBox(side, -1.5F, -1.0F, 0.5F, 0.5F, 2.0F)
						.addBox(side, -3.0F, -1.0F, 0.5F, 1.5F, 0.5F)
						.addBox(side, -3.0F, 0.5F, 0.5F, 1.5F, 0.5F);
				}
			},
			root -> root.addBox(4.4F, -1.1F, -0.45F, 0.7F, 0.9F, 0.9F)
				.addBox(-5.1F, -1.1F, -0.45F, 0.7F, 0.9F, 0.9F),
			null));

		models.put(Piece.STUDS, parts(slim, "head",
			root -> root.addBox(4.5F, -2.6F, -0.6F, 0.3F, 1.2F, 1.2F)
				.addBox(-4.8F, -2.6F, -0.6F, 0.3F, 1.2F, 1.2F),
			root -> root.addBox(4.6F, -2.5F, -0.5F, 0.5F, 1.0F, 1.0F)
				.addBox(-5.1F, -2.5F, -0.5F, 0.5F, 1.0F, 1.0F),
			null));

		models.put(Piece.CHAIN, parts(slim, "body",
			root -> {
				chain(root, 5, 0.6F, 0.3F, -2.55F);
				root.addBox(2.7F, -0.3F, -2.55F, 0.6F, 0.3F, 5.1F)
					.addBox(-3.3F, -0.3F, -2.55F, 0.6F, 0.3F, 5.1F);
			},
			root -> root.addBox(-0.75F, 4.5F, -2.8F, 1.5F, 1.5F, 0.5F),
			null));

		models.put(Piece.ICED_CHAIN, parts(slim, "body",
			root -> {
				chain(root, 6, 0.9F, 0.4F, -2.6F);
				root.addBox(2.55F, -0.4F, -2.6F, 0.9F, 0.4F, 5.2F)
					.addBox(-3.45F, -0.4F, -2.6F, 0.9F, 0.4F, 5.2F);
			},
			root -> {
				chain(root, 6, 0.5F, 0.2F, -2.8F);
				root.addBox(-1.0F, 4.6F, -3.0F, 2.0F, 2.0F, 0.6F);
			},
			null));

		models.put(Piece.BRACELET, parts(slim, "left_arm",
			root -> root.addBox(-1.35F, 7.8F, -2.35F, arm + 0.7F, 1.0F, 4.7F),
			root -> root.addBox(outside + 0.3F, 7.85F, -0.45F, 0.4F, 0.9F, 0.9F),
			null));

		Consumer<CubeListBuilder> watchMetal = root -> root
			.addBox(-1.3F, 7.8F, -2.3F, arm + 0.6F, 0.9F, 4.6F)
			.addBox(outside + 0.3F, 7.2F, -1.1F, 0.5F, 2.1F, 2.2F);
		Consumer<CubeListBuilder> watchFace = root -> root.addBox(outside + 0.8F, 7.4F, -0.9F, 0.05F, 1.7F, 1.8F);

		models.put(Piece.WATCH, parts(slim, "left_arm", watchMetal, watchFace, null));

		float bezel = outside + 0.8F;
		models.put(Piece.ICED_WATCH, parts(slim, "left_arm", watchMetal, watchFace,
			root -> root
				.addBox(bezel, 7.1F, -1.15F, 0.15F, 0.3F, 2.3F)
				.addBox(bezel, 9.1F, -1.15F, 0.15F, 0.3F, 2.3F)
				.addBox(bezel, 7.4F, -1.15F, 0.15F, 1.7F, 0.25F)
				.addBox(bezel, 7.4F, 0.9F, 0.15F, 1.7F, 0.25F)
				.addBox(-1.4F, 8.05F, -2.4F, arm + 0.8F, 0.4F, 4.8F)));
	}

	/** A V of links from both shoulders down to the middle of the chest, {@code steps} links per side. */
	private static void chain(CubeListBuilder root, int steps, float size, float depth, float z) {
		for (int i = 0; i <= steps; i++) {
			float t = (float) i / steps;
			float cx = 3.0F * (1.0F - t);
			float cy = 4.5F * t;
			root.addBox(cx - size / 2, cy - size / 2, z, size, size, depth);
			if (i < steps) {
				root.addBox(-cx - size / 2, cy - size / 2, z, size, size, depth);
			}
		}
	}

	public static JewelryModels create(boolean slim) {
		return new JewelryModels(slim);
	}

	public Parts get(Piece piece) {
		return models.get(piece);
	}

	private static Parts parts(boolean slim, String part, Consumer<CubeListBuilder> metal, Consumer<CubeListBuilder> accent,
		@Nullable Consumer<CubeListBuilder> diamonds) {
		return new Parts(bake(slim, part, metal), bake(slim, part, accent), diamonds == null ? null : bake(slim, part, diamonds));
	}

	private static PlayerModel bake(boolean slim, String part, Consumer<CubeListBuilder> cubes) {
		MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, slim);
		PartDefinition root = mesh.getRoot().clearRecursively();
		CubeListBuilder builder = CubeListBuilder.create().texOffs(0, 0);
		cubes.accept(builder);
		root.getChild(part).addOrReplaceChild("jewelry", builder, PartPose.ZERO);
		return new PlayerModel(LayerDefinition.create(mesh, 32, 32).bakeRoot(), slim);
	}
}
