package com.raphydaphy.thaumcraft.init;

import com.raphydaphy.thaumcraft.Thaumcraft;
import com.raphydaphy.thaumcraft.block.BlockArcaneWorktable;
import com.raphydaphy.thaumcraft.block.BlockBase;
import com.raphydaphy.thaumcraft.block.BlockModLeaves;
import com.raphydaphy.thaumcraft.block.BlockModLog;
import com.raphydaphy.thaumcraft.block.BlockOre;
import com.raphydaphy.thaumcraft.block.BlockTable;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ModBlocks 
{
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":log_greatwood")
    public static BlockModLog log_greatwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":log_silverwood")
    public static BlockModLog log_silverwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":leaves_greatwood")
    public static BlockModLeaves leaves_greatwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":leaves_silverwood")
    public static BlockModLeaves leaves_silverwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":planks_greatwood")
    public static BlockBase planks_greatwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":planks_silverwood")
    public static BlockBase planks_silverwood;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":ore_infused")
    public static BlockOre ore_infused;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":table")
    public static BlockTable table;
	
	@GameRegistry.ObjectHolder(Thaumcraft.MODID + ":arcane_worktable")
    public static BlockArcaneWorktable arcane_worktable;
	
	@SideOnly(Side.CLIENT)
    public static void initModels() {
		log_greatwood.initModel();
		log_silverwood.initModel();
		leaves_greatwood.initModel();
		leaves_silverwood.initModel();
        planks_greatwood.initModel();
        planks_silverwood.initModel();
        
        ore_infused.initModel();
        
        table.initModel();
        arcane_worktable.initModel();
    }
}
