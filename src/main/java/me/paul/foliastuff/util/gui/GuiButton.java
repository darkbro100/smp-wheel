package me.paul.foliastuff.util.gui;

import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.function.*;

public class GuiButton {

	private GuiPage gui;
	private GuiClickEvent listener;
	private ItemStack item;
	private int slot;

	/**
	 * Constructor for the GuiButton
	 * @param item ItemStack icon for this GuiButton
	 */
	public GuiButton(ItemStack item) {
		this.item = item;
	}

	/**
	 * Sets the listener for this button
	 * @param consumer Consumer for the InventoryClickEvent
	 */
	public void setListener(BiConsumer<InventoryClickEvent, Player> consumer) {
		this.listener = new GuiClickEvent() {
			@Override
			public void onClick(InventoryClickEvent event) {
				consumer.accept(event, (Player) event.getWhoClicked());
			}
		};
	}

	public GuiPage getGui() {
		return gui;
	}

	public void setGui(GuiPage gui) {
		this.gui = gui;
	}

	public GuiClickEvent getListener() {
		return listener;
	}

	public void setListener(GuiClickEvent listener) {
		this.listener = listener;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

}
