package com.raphydaphy.arcanemagic.client.particle;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;

public class ParticleRenderer {
	ArrayList<Particle> particles = new ArrayList<Particle>();

	public void updateParticles() {
		boolean doRemove = false;
		
		Iterator<Particle> iter = particles.iterator();
		while(iter.hasNext())
		{
			Particle i = iter.next();
			doRemove = true;
			if (i != null)
			{
				if (i instanceof IModParticle) {
					if (i.isAlive())
					{
						i.onUpdate();
						doRemove = false;
					}
				}
			}
			if (doRemove)
			{
				iter.remove();
			}
		}
	}

	public void renderParticles(EntityPlayer dumbplayer, float partialTicks) {
		float f = ActiveRenderInfo.getRotationX();
		float f1 = ActiveRenderInfo.getRotationZ();
		float f2 = ActiveRenderInfo.getRotationYZ();
		float f3 = ActiveRenderInfo.getRotationXY();
		float f4 = ActiveRenderInfo.getRotationXZ();
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			Particle.cameraViewDir = player.getLook(partialTicks);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.alphaFunc(516, 0.003921569F);
			GlStateManager.disableCull();

			GlStateManager.depthMask(false);

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buffer = tess.getBuffer();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			for (Particle particle3 : particles) {
				if (particle3 instanceof IModParticle) {
					if (!((IModParticle) particle3).isAdditive()) {
						particle3.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
					}
				}
			}
			tess.draw();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			for (Particle particle2 : particles) {
				if (particle2 != null) {
					if (((IModParticle) particle2).isAdditive()) {
						particle2.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
					}
				}
			}
			tess.draw();

			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
		}
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}
}