package com.raphydaphy.arcanemagic.intergration;

import java.util.ArrayList;
import java.util.List;

import com.raphydaphy.arcanemagic.api.ArcaneMagicAPI;
import com.raphydaphy.arcanemagic.api.recipe.ElementalCraftingRecipe;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.config.Constants;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JEIPlugin
public class ArcaneMagicJEIPlugin implements IModPlugin
{
	public static final String ELEMENTAL_CRAFTING_UID = "arcanemagic.elementalcrafting";
	public void register(IModRegistry registry)
	{
		registry.addRecipes(ArcaneMagicAPI.elemental_crafting_recipes, ELEMENTAL_CRAFTING_UID);
		
		registry.handleRecipes(ElementalCraftingRecipe.class, recipe -> new ElementalCraftingRecipeWrapper(recipe),  ELEMENTAL_CRAFTING_UID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new CategoryElementalCrafting(guiHelper));
	}

	public class CategoryElementalCrafting implements IRecipeCategory<IRecipeWrapper>
	{
		private static final int craftOutputSlot = 0;
		private static final int craftInputSlot1 = 1;

		public static final int width = 116;
		public static final int height = 54;

		private final IDrawable background;
		private final ICraftingGridHelper craftingGridHelper;

		public CategoryElementalCrafting(IGuiHelper guiHelper)
		{
			ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
			background = guiHelper.createDrawable(location, 0, 60, width, height);
			craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
		}

		@Override
		public String getUid()
		{
			return ELEMENTAL_CRAFTING_UID;
		}

		@Override
		public String getTitle()
		{
			return I18n.format("gui.arcanemagic.elemental_crafting");
		}

		@Override
		public String getModName()
		{
			return I18n.format("itemGroup.arcanemagic");
		}

		@Override
		public IDrawable getBackground()
		{
			return background;
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients)
		{
			IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

			guiItemStacks.init(craftOutputSlot, false, 94, 18);

			for (int y = 0; y < 3; ++y)
			{
				for (int x = 0; x < 3; ++x)
				{
					int index = craftInputSlot1 + x + (y * 3);
					guiItemStacks.init(index, true, x * 18, y * 18);
				}
			}

			if (recipeWrapper instanceof ICustomCraftingRecipeWrapper)
			{
				ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper) recipeWrapper;
				customWrapper.setRecipe(recipeLayout, ingredients);
				return;
			}

			List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
			List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

			if (recipeWrapper instanceof IShapedCraftingRecipeWrapper)
			{
				IShapedCraftingRecipeWrapper wrapper = (IShapedCraftingRecipeWrapper) recipeWrapper;
				craftingGridHelper.setInputs(guiItemStacks, inputs, wrapper.getWidth(), wrapper.getHeight());
			} else
			{
				craftingGridHelper.setInputs(guiItemStacks, inputs);
				recipeLayout.setShapeless();
			}
			guiItemStacks.set(craftOutputSlot, outputs.get(0));
		}

	}

	public class ElementalCraftingRecipeWrapper implements IShapedCraftingRecipeWrapper
	{
		private final ElementalCraftingRecipe recipe;

		public ElementalCraftingRecipeWrapper(ElementalCraftingRecipe recipe)
		{
			this.recipe = recipe;
		}

		@Override
		public void getIngredients(IIngredients ingredients)
		{
			ItemStack recipeOutput = recipe.getOutput();

			try
			{
				List<List<ItemStack>> inputLists = new ArrayList<List<ItemStack>>();

				for (int y = 0; y < 3; y++)
				{
					List<ItemStack> thisRow = new ArrayList<ItemStack>();
					thisRow.add(recipe.getInput()[0][y]);
					thisRow.add(recipe.getInput()[1][y]);
					thisRow.add(recipe.getInput()[2][y]);
				}
				ingredients.setInputLists(ItemStack.class, inputLists);
				ingredients.setOutput(ItemStack.class, recipeOutput);
			} catch (RuntimeException e)
			{
				throw new BrokenCraftingRecipeException("An elemental crafting recipe went bad", e);
			}
		}

		@Override
		public int getWidth()
		{
			return 116;
		}

		@Override
		public int getHeight()
		{
			return 54;
		}

	}
}
