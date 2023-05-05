package com.tom.trading.jei;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;

@SuppressWarnings("rawtypes")
public interface JeiGhostIngredientHandlerPlatform<T extends Screen> extends IGhostIngredientHandler<T> {

	@SuppressWarnings("unchecked")
	@Override
	default <I> List<Target<I>> getTargetsTyped(T gui, ITypedIngredient<I> ingredient, boolean doStart) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			ItemStack stack = (ItemStack) ingredient.getIngredient();
			return (List) getTargets(gui, stack, doStart);
		}
		return Collections.emptyList();
	}

	List<Target<ItemStack>> getTargets(T gui, ItemStack stack, boolean doStart);
}
