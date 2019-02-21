package com.raphydaphy.arcanemagic.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class InventoryBlockEntity extends BlockEntity implements Inventory
{
	private DefaultedList<ItemStack> contents;
	private final int size;

	public InventoryBlockEntity(BlockEntityType<?> type, int size)
	{
		super(type);

		this.contents = DefaultedList.create(size, ItemStack.EMPTY);
		this.size = size;
	}

	@Override
	public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		contents = DefaultedList.create(size, ItemStack.EMPTY);
		InventoryUtil.deserialize(tag, contents);
	}

	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);
		writeContents(tag);
		return tag;
	}

	public void writeContents(CompoundTag tag)
	{
		InventoryUtil.serialize(tag, contents);
	}

	@Override
	public int getInvSize()
	{
		return size;
	}

	@Override
	public boolean isInvEmpty()
	{
		for (ItemStack stack : contents)
		{
			if (!stack.isEmpty())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getInvStack(int slot)
	{
		return contents.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int count)
	{
		ItemStack stack = InventoryUtil.splitStack(contents, slot, count);

		if (!stack.isEmpty())
		{
			markDirty();
		}

		return stack;
	}

	@Override
	public ItemStack removeInvStack(int slot)
	{
		return InventoryUtil.removeStack(contents, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack)
	{
		contents.set(slot, stack);

		// TODO: ensure this won't delete overflow items
		if (stack.getAmount() > this.getInvMaxStackAmount())
		{
			stack.setAmount(this.getInvMaxStackAmount());
		}

		markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity var1)
	{
		return false;
	}

	@Override
	public void clear()
	{
		contents.clear();
		markDirty();
	}
}