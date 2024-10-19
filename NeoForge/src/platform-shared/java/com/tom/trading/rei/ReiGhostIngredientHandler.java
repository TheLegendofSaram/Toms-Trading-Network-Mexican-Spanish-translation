package com.tom.trading.rei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;

import com.tom.trading.menu.AbstractFilteredMenu;
import com.tom.trading.menu.slot.PhantomSlot;
import com.tom.trading.screen.AbstractFilteredScreen;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;

@SuppressWarnings("rawtypes")
public class ReiGhostIngredientHandler implements DraggableStackVisitor<AbstractFilteredScreen> {

	@Override
	public <R extends Screen> boolean isHandingScreen(R screen) {
		return screen instanceof AbstractFilteredScreen;
	}

	@Override
	public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<AbstractFilteredScreen> context, DraggableStack stack) {
		List<DropTarget> targets = getTargets(context.getScreen(), stack.get());
		return targets.stream().map(target -> BoundsProvider.ofRectangle(target.getArea()));
	}

	@Override
	public DraggedAcceptorResult acceptDraggedStack(DraggingContext<AbstractFilteredScreen> context, DraggableStack stack) {
		var targets = getTargets(context.getScreen(), stack.get());
		var pos = context.getCurrentPosition();

		for (var target : targets) {
			if (target.getArea().contains(pos)) {
				target.accept(stack);
				return DraggedAcceptorResult.ACCEPTED;
			}
		}

		return DraggedAcceptorResult.PASS;
	}

	private <I> List<DropTarget> getTargets(AbstractFilteredScreen gui, EntryStack<?> ingredient) {
		if (ingredient.getType() == VanillaEntryTypes.ITEM) {
			List<DropTarget> targets = new ArrayList<>();
			for (Slot slot : gui.getMenu().slots) {
				if (slot instanceof PhantomSlot) {
					targets.add(new SlotTarget(gui, slot));
				}
			}
			return targets;
		}
		return Collections.emptyList();
	}

	private static class SlotTarget implements DropTarget {
		private Slot slot;
		private Rectangle area;
		private AbstractFilteredScreen gui;

		public SlotTarget(AbstractFilteredScreen gui, Slot slot) {
			this.slot = slot;
			this.gui = gui;
			this.area = new Rectangle(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
		}

		@Override
		public Rectangle getArea() {
			return area;
		}

		@Override
		public void accept(DraggableStack ingredient) {
			((AbstractFilteredMenu) gui.getMenu()).setPhantom(slot, ingredient.get().castValue());
		}
	}

	interface DropTarget {
		Rectangle getArea();
		void accept(DraggableStack stack);
	}
}
