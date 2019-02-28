package com.raphydaphy.arcanemagic.block.entity;

import com.raphydaphy.arcanemagic.block.entity.base.DoubleBlockEntity;
import com.raphydaphy.arcanemagic.client.particle.ParticleRenderer;
import com.raphydaphy.arcanemagic.client.particle.ParticleUtil;
import com.raphydaphy.arcanemagic.init.ModRegistry;
import com.raphydaphy.arcanemagic.network.ArcaneMagicPacketHandler;
import com.raphydaphy.arcanemagic.network.ClientBlockEntityUpdatePacket;
import com.raphydaphy.arcanemagic.util.ArcaneMagicUtils;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.particle.Particle;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.cooking.BlastingRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class SmelterBlockEntity extends DoubleBlockEntity implements Tickable
{
	private static final int TOTAL_SMELTING_TIME = 150;
	private static final String SMELT_TIME_KEY = "SmeltTime";
	private final int[] slots = {0, 1, 2};

	public long ticks = 0;
	private int smeltTime = 0;

	public SmelterBlockEntity()
	{
		super(ModRegistry.SMELTER_TE, 3);
	}

	@Override
	public void tick()
	{
		if (world.isClient)
		{
			ticks++;
		}
		if (!setBottom)
		{
			bottom = ArcaneMagicUtils.isBottomBlock(world, pos, ModRegistry.SMELTER);
			setBottom = true;
		}

		if (world.isClient && bottom)
		{
			doParticles();
		}
		if (bottom && smeltTime > 0)
		{
			smeltTime++;

			if (world.isClient)
			{
			} else
			{
				if (smeltTime % 10 == 0)
				{
					markDirty();
				}

				if (smeltTime >= TOTAL_SMELTING_TIME)
				{
					Optional<BlastingRecipe> optionalRecipe = this.world.getRecipeManager().get(RecipeType.BLASTING, new BasicInventory(getInvStack(0)), this.world);

					if (optionalRecipe.isPresent())
					{
						BlastingRecipe recipe = optionalRecipe.get();
						setInvStack(0, ItemStack.EMPTY);
						setInvStack(1, recipe.getOutput().copy());
						setInvStack(2, recipe.getOutput().copy());
					}

					smeltTime = 0;
					markDirty();
				}
			}
		}
	}

	private void doParticles()
	{
		float inverseSpread = 100;
		for (int i = 0; i < 2; i++)
		{
			ParticleUtil.spawnSmokeParticle(world, pos.getX() + 0.4f + ParticleUtil.random.nextFloat() / 5f, pos.getY() + 1.6f, pos.getZ() + 0.55f + ParticleUtil.random.nextFloat() / 5f,
					(float) ParticleUtil.random.nextGaussian() / inverseSpread, 0.05f + ParticleUtil.random.nextFloat() * 0 / 20f, (float) ParticleUtil.random.nextGaussian() / inverseSpread,
					1, 1, 1, 1,0.3f, 250);
		}
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
		ArcaneMagicPacketHandler.sendToAllAround(new ClientBlockEntityUpdatePacket(toInitialChunkDataTag()), world, getPos(), 300);
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket()
	{
		CompoundTag tag = super.toInitialChunkDataTag();
		writeContents(tag);
		return new BlockEntityUpdateS2CPacket(getPos(), -1, tag);
	}

	@Override
	public CompoundTag toInitialChunkDataTag()
	{
		CompoundTag tag = super.toInitialChunkDataTag();
		writeContents(tag);
		return tag;
	}

	@Override
	public void writeContents(CompoundTag tag)
	{
		super.writeContents(tag);
		if (bottom)
		{
			tag.putInt(SMELT_TIME_KEY, smeltTime);
		}
	}

	@Override
	public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		if (bottom)
		{
			smeltTime = tag.getInt(SMELT_TIME_KEY);
		}
	}

	public int getSmeltTime()
	{
		return smeltTime;
	}

	@Override
	public int getInvMaxStackAmount()
	{
		return 1;
	}

	@Override
	public boolean isValidInvStackBottom(int slot, ItemStack item)
	{
		if (slot == 9)
		{
			if (!getInvStack(1).isEmpty() || !getInvStack(2).isEmpty())
			{
				return false;
			}
		}
		return getInvStack(slot).isEmpty() && this.world.getRecipeManager().get(RecipeType.BLASTING, new BasicInventory(item), this.world).isPresent();
	}

	public boolean startSmelting(boolean simulate)
	{
		if (smeltTime <= 0 && !getInvStack(0).isEmpty() && getInvStack(1).isEmpty() && getInvStack(2).isEmpty())
		{
			Optional<BlastingRecipe> optionalRecipe = this.world.getRecipeManager().get(RecipeType.BLASTING, new BasicInventory(getInvStack(0)), this.world);
			if (optionalRecipe.isPresent())
			{
				if (!simulate)
				{
					if (!world.isClient)
					{
						smeltTime = 1;
						markDirty();
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int[] getInvAvailableSlots(Direction dir)
	{
		return dir == Direction.UP ? new int[0] : slots;
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir)
	{
		return bottom && getInvStack(1).isEmpty() && getInvStack(2).isEmpty() && slot == 0 && isValidInvStack(slot, stack);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir)
	{
		return bottom && slot != 0;
	}
}
