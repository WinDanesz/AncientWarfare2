package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.registry.FactionTradeListRegistry;
import net.shadowmage.ancientwarfare.npc.registry.FactionTradeListTemplate;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;
import net.shadowmage.ancientwarfare.npc.trade.Trade;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GuiNpcFactionTradeSetup extends GuiContainerBase<ContainerNpcFactionTradeSetup> {

	private CompositeScrolled area;
	private FactionTradeList tradeList;

	public GuiNpcFactionTradeSetup(ContainerBase container) {
		super(container, 320, 240);
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 0, xSize, ySize - 16 - 4 - 4 * 18);
		addGuiElement(area);

		Button templateButton = new Button(170, ySize - 85, xSize - 174, 12, "guistrings.select_trade_template") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiNpcFactionTradeSetup.this, "",
						s -> s, () -> getTemplateList(getContainer().getEntity().getFaction()), s -> {
					setTradeListFromTemplate(s, getContainer().getEntity().getFaction());
					refreshGui();
				}, true, new ContainerBase(player)));
			}

			private void setTradeListFromTemplate(String templateName, String factionName) {
				GuiNpcFactionTradeSetup.this.getContainer().setTradeList(
						FactionTradeListRegistry.getDefaults().getOrDefault(templateName,
								FactionTradeListRegistry.getFactionDefaults(factionName).getOrDefault(templateName, FactionTradeListTemplate.EMPTY))
								.toTradeList()
				);
			}

			private List<String> getTemplateList(String factionName) {
				HashSet<String> templates = new HashSet<>(FactionTradeListRegistry.getDefaults().keySet());
				templates.addAll(FactionTradeListRegistry.getFactionDefaults(factionName).keySet());
				return templates.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
			}
		};

		addGuiElement(templateButton);

		Button templateSaveButton = new Button(170, ySize - 69, xSize - 174, 12, "guistrings.save_to_trade_template") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiTradeTemplateSave(GuiNpcFactionTradeSetup.this));
				refreshGui();
			}
		};
		addGuiElement(templateSaveButton);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		tradeList = getContainer().tradeList;
		if (tradeList == null) {
			return;
		}

		int totalHeight = 8;
		for (int i = 0; i < tradeList.size(); i++) {
			totalHeight = addTrade(tradeList.get(i), totalHeight, i);
		}

		Button newTradeButton = new Button(8, totalHeight, xSize - 8 - 16, 12, "guistrings.new_trade") {
			@Override
			protected void onPressed() {
				tradeList.addNewTrade();
				getContainer().tradesChanged = true;
				refreshGui();
			}
		};
		area.addGuiElement(newTradeButton);
		totalHeight += 12;

		area.setAreaSize(totalHeight);
	}

	private int addTrade(final Trade trade, int startHeight, final int tradeNum) {
		int gridX = 0;
		int gridY = 0;
		for (int i = 0; i < trade.size(); i++) {
			int slotX = gridX * 18 + 8;
			int slotY = gridY * 18 + startHeight;
			addTradeInputSlot(trade, slotX, slotY, i);
			slotX += 3 * 18 + 9;
			addTradeOutputSlot(trade, slotX, slotY, i);
			gridX++;
			if (gridX >= 3) {
				gridX = 0;
				gridY++;
			}
		}

		addTradeControls((FactionTrade) trade, startHeight, tradeNum);

		startHeight += 18 * gridY;//input/output grid size
		area.addGuiElement(new Line(0, startHeight + 1, xSize, startHeight + 1, 1, 0x000000ff));
		startHeight += 5;//separator line and padding
		return startHeight;
	}

	private void addTradeControls(final FactionTrade trade, int startHeight, final int tradeNum) {
		startHeight -= 1;//offset by 1 to lineup better with the item slot boxes, as they align to the inner slot rather than border
		int startWidth = 8 + 3 * 18;
		if (trade.size() < 3) {
			startWidth += (trade.size() - 3) * 18;
		}
		int infoX = startWidth * 2 + 5;
		Button upButton = new Button(infoX, startHeight, 55, 12, "guistrings.up") {
			@Override
			protected void onPressed() {
				tradeList.increment(tradeNum);
				refreshGui();
				getContainer().tradesChanged = true;
			}
		};
		area.addGuiElement(upButton);

		Button downButton = new Button(infoX, startHeight + 3 * 18 - 12, 55, 12, "guistrings.down") {
			@Override
			protected void onPressed() {
				tradeList.decrement(tradeNum);
				refreshGui();
				getContainer().tradesChanged = true;
			}
		};
		area.addGuiElement(downButton);

		Button delete = new Button(infoX, startHeight + 21, 55, 12, "guistrings.delete") {
			@Override
			protected void onPressed() {
				tradeList.remove(tradeNum);
				refreshGui();
				getContainer().tradesChanged = true;
			}
		};
		area.addGuiElement(delete);

		area.addGuiElement(new Label(startWidth + 1, startHeight + 20, ">"));

		area.addGuiElement(new Label(infoX + 55 + 4, startHeight + 1, "guistrings.max_trades"));

		area.addGuiElement(new Label(infoX + 55 + 4, startHeight + 18 + 1, "guistrings.refill_frequency"));

		NumberInput tradeInput = new NumberInput(infoX + 55 + 4 + 60, startHeight, 40, trade.getMaxAvailable(), this) {
			@Override
			public void onValueUpdated(float value) {
				trade.setMaxAvailable((int) value);
				getContainer().tradesChanged = true;
			}
		};
		tradeInput.setIntegerValue();
		area.addGuiElement(tradeInput);

		NumberInput refillInput = new NumberInput(infoX + 55 + 4 + 60, startHeight + 18, 40, trade.getRefillFrequency(), this) {
			@Override
			public void onValueUpdated(float value) {
				trade.setRefillFrequency((int) value);
				getContainer().tradesChanged = true;
			}
		};
		refillInput.setIntegerValue();
		refillInput.setAllowNegative();
		area.addGuiElement(refillInput);
	}

	private void addTradeInputSlot(final Trade trade, int x, int y, final int slotNum) {
		@Nonnull ItemStack stack = trade.getInputStack(slotNum);
		stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		final ItemSlot slot = new ItemSlot(x, y, stack, this) {
			@Override
			public void onSlotClicked(ItemStack stack, boolean rightClicked) {
				stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
				setItem(stack);
				trade.setInputStack(slotNum, stack);
				getContainer().tradesChanged = true;
			}
		};
		if (stack.isEmpty()) {
			slot.addTooltip("guistrings.npc.trade_input_slot");
		}
		area.addGuiElement(slot);
	}

	private void addTradeOutputSlot(final Trade trade, int x, int y, final int slotNum) {
		@Nonnull ItemStack stack = trade.getOutputStack(slotNum);
		stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		final ItemSlot slot = new ItemSlot(x, y, stack, this) {
			@Override
			public void onSlotClicked(ItemStack stack, boolean rightClicked) {
				stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
				setItem(stack);
				trade.setOutputStack(slotNum, stack);
				getContainer().tradesChanged = true;
			}
		};
		if (stack.isEmpty()) {
			slot.addTooltip("guistrings.npc.trade_output_slot");
		}
		area.addGuiElement(slot);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().onGuiClosed();
		return super.onGuiCloseRequested();
	}

}
