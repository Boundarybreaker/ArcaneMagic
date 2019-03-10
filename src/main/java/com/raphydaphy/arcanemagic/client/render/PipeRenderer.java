package com.raphydaphy.arcanemagic.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.block.PipeBlock;
import com.raphydaphy.arcanemagic.block.entity.PipeBlockEntity;
import com.raphydaphy.arcanemagic.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class PipeRenderer extends BlockEntityRenderer<PipeBlockEntity>
{
	private static Identifier tex = new Identifier(ArcaneMagic.DOMAIN, "textures/block/pipe.png");
	private static RenderUtils.TextureBounds[] centerSmall = {
			new RenderUtils.TextureBounds(0, 0, 4, 4), // Bottom
			new RenderUtils.TextureBounds(0, 0, 4, 4), // Top
			new RenderUtils.TextureBounds(0, 0, 4, 4), // North
			new RenderUtils.TextureBounds(0, 0, 4, 4), // South
			new RenderUtils.TextureBounds(0, 0, 4, 4), // West
			new RenderUtils.TextureBounds(0, 0, 4, 4)}; // East
	private static RenderUtils.TextureBounds[] upDown = {
			new RenderUtils.TextureBounds(0, 0, 0, 0), // Bottom
			new RenderUtils.TextureBounds(0, 0, 0, 0), // Top
			new RenderUtils.TextureBounds(0, 4, 4, 10), // North
			new RenderUtils.TextureBounds(0, 4, 4, 10), // South
			new RenderUtils.TextureBounds(0, 4, 4, 10), // West
			new RenderUtils.TextureBounds(0, 4, 4, 10)}; // East
	private static RenderUtils.TextureBounds[] northSouth = {
			new RenderUtils.TextureBounds(4, 0, 10, 4), // Bottom
			new RenderUtils.TextureBounds(4, 0, 10, 4), // Top
			new RenderUtils.TextureBounds(0, 0, 0, 0), // North
			new RenderUtils.TextureBounds(0, 0, 0, 0), // South
			new RenderUtils.TextureBounds(4, 0, 10, 4), // West
			new RenderUtils.TextureBounds(4, 0, 10, 4)}; // East
	private static RenderUtils.TextureBounds[] westEast = {
			new RenderUtils.TextureBounds(0, 4, 4, 10), // Bottom
			new RenderUtils.TextureBounds(0, 4, 4, 10), // Top
			new RenderUtils.TextureBounds(4, 0, 10, 4), // North
			new RenderUtils.TextureBounds(4, 0, 10, 4), // South
			new RenderUtils.TextureBounds(0, 0, 0, 0), // West
			new RenderUtils.TextureBounds(0, 0, 0, 0)}; // East

	public void render(PipeBlockEntity entity, double renderX, double renderY, double renderZ, float partialTicks, int destroyStage)
	{
		super.render(entity, renderX, renderY, renderZ, partialTicks, destroyStage);


		if (entity != null)
		{
			GlStateManager.pushMatrix();
			MinecraftClient.getInstance().getTextureManager().bindTexture(tex);
			GlStateManager.disableCull();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder builder = tess.getBufferBuilder();

			builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR_NORMAL);
			double pixel = 1d / 16d;

			if (entity.getWorld() != null)
			{
				BlockState state = entity.getWorld().getBlockState(entity.getPos());

				if (state.getProperties().contains(PipeBlock.UP))
				{
					if (state.get(PipeBlock.UP))
					{
						RenderUtils.renderBox(builder, renderX + pixel * 6, renderY + pixel * 10, renderZ + pixel * 6, renderX + pixel * 10, renderY + 1, renderZ + pixel * 10, upDown, new int[]{1, 1, 1, 1, 1, 1});
					}
					if (state.get(PipeBlock.DOWN))
					{
						RenderUtils.renderBox(builder, renderX + pixel * 6, renderY, renderZ + pixel * 6, renderX + pixel * 10, renderY + pixel * 6, renderZ + pixel * 10, upDown, new int[]{-1, -1, 1, 1, 1, 1});
					}
					if (state.get(PipeBlock.NORTH))
					{
						RenderUtils.renderBox(builder, renderX + pixel * 6, renderY + pixel * 6, renderZ, renderX + pixel * 10, renderY + pixel * 10, renderZ + pixel * 6, northSouth, new int[]{1, 1, 1, 1, 1, 1});
					}
					if (state.get(PipeBlock.SOUTH))
					{
						RenderUtils.renderBox(builder, renderX + pixel * 6, renderY + pixel * 6, renderZ + pixel * 10, renderX + pixel * 10, renderY + pixel * 10, renderZ + 1, northSouth, new int[]{1, 1, -1, -1, 1, 1});
					}
					if (state.get(PipeBlock.WEST))
					{
						RenderUtils.renderBox(builder, renderX, renderY + pixel * 6, renderZ + pixel * 6, renderX + pixel * 6, renderY + pixel * 10, renderZ + pixel * 10, westEast, new int[]{1, 1, 1, 1, 1, 1});
					}
					if (state.get(PipeBlock.EAST))
					{
						RenderUtils.renderBox(builder, renderX + pixel * 10, renderY + pixel * 6, renderZ + pixel * 6, renderX + 1, renderY + pixel * 10, renderZ + pixel * 10, westEast, new int[]{1, 1, 1, 1, -1, -1});
					}
				}
			}

			RenderUtils.renderBox(builder, renderX + pixel * 6, renderY + pixel * 6, renderZ + pixel * 6, renderX + pixel * 10, renderY + pixel * 10, renderZ + pixel * 10, centerSmall, new int[]{1, 1, 1, 1, 1, 1});

			tess.draw();

			GlStateManager.popMatrix();
		}
	}
}
